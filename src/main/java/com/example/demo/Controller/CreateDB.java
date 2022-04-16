package com.example.demo.Controller;

import com.example.demo.Entity.Category;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Characteristic;
import com.example.demo.Entity.Type;
import com.example.demo.Repositories.CategoryRepository;
import com.example.demo.Repositories.ProductRepository;
import com.example.demo.Repositories.CharacteristicRepository;
import com.example.demo.Repositories.TypeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Api(tags = "Create DB")
@CrossOrigin
@RestController
public class CreateDB {

    @Autowired
    TypeRepository typeRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CharacteristicRepository characteristicRepository;

    int amountOfCategories = 0;


    @Async
    @ApiOperation(value = "Парсинг данных (Метод выполняется ассинхронно, " +
            "все данные выводятся в консоль на сервере)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---")
    })
    @GetMapping("/createDB")
    public void createDB() throws IOException {
        Document document = selectDocumentByHref("https://www.holodilnik.ru/");
        Elements types = document.getElementsByClass( "menu-categories__item");
        for(int i = 0; i < 8;i++){
            if(i!=2 && i!=4 && i!=6){
                Element element = types.get(i);
                workWithSubtypes(element.child(0).text(), element.child(0).attr("data-href"));
            }

        }

        System.out.println("DB was created correctly");
    }


    public void workWithSubtypes(String nameOfType, String href)  {

        try{
            typeRepository.save(createType(nameOfType));
        }
        catch (DataIntegrityViolationException e){

        }

        Document document = selectDocumentByHref("https:" + href);

        Elements allSubtypes = document.getElementsByClass("categories__item-list__title");
//        AtomicBoolean flag = new AtomicBoolean(false);
        allSubtypes.forEach(element -> {
            if(element.child(0).text().equals(nameOfType)){
                searchingCategories(nameOfType, element.child(0).attr("href"));
//                flag.set(true);
            }
        });


//        if(!flag.get()){
//            Element element = allSubtypes.get(2);
//            searchingCategories(nameOfType, element.child(0).attr("href"));
//        }

    }

    public void searchingCategories(String nameOfType, String href){
        Document document = selectDocumentByHref("https:" + href);

        Element fieldWithCategories = document.getElementById("div_filter_vendor");
        Elements allCategories = fieldWithCategories.
                getElementsByClass("field-checkbox__label form-check-label beforeSelect");
        for(int i = 0; i < allCategories.size(); i++){
            Element element = allCategories.get(i);
            categoryRepository.save(createCategory(nameOfType, element.text().substring(0, element.text().indexOf(" "))));
            inOneCategory(
                    nameOfType,
                    element.text().substring(0, element.text().indexOf(" ")),
                    element.child(1).attr("href"));
        }
    }


    public void inOneCategory(String nameOfType, String categoryName, String href){

        Document document = selectDocumentByHref("https:" + href);
        Elements fields = document.getElementsByClass("goods-tile preview-product");

        // Наименования продуктов
        Elements names = fields.select("div[class=product-name]");
        List<String> nameOfProductsList = new ArrayList<>();
        names.forEach(element -> nameOfProductsList.add(element.text()));

        // Пути до картинок продуктов
        Elements pictures = fields.select("div[class=col product-image]");
        Elements imgs = pictures.select("img");
        List<String> pathOfPicturesList = new ArrayList<>();
        imgs.forEach(img -> pathOfPicturesList.add("https:" + img.attr("src")));

        // Дополнительная информация о продуктах
        Elements tables = fields.select("table[class=table table-borderless]");
        Map<Integer, List<List<String>>> map = new HashMap<>();
        for(Element table: tables){
            Elements trs = table.select("tr");

            List<List<String>> allInfo= new ArrayList<>();
            for(Element tr: trs){
                List<String> oneLine = new ArrayList<>();
                if(tr.childrenSize()==2) {
                    oneLine.add(tr.child(0).text());
                    oneLine.add(tr.child(1).text());
                    allInfo.add(oneLine);
                }
            }
            map.put(tables.indexOf(table), allInfo);
        }

        // Цена продуктов
        Elements prices = fields.select("div[class=price]");
        List<String> priceOfProductsList = new ArrayList<>();
        for(Element price: prices){
            String temp = price.text().replaceAll("[ руб.]","");
            if(temp.length()>6){
                temp = temp.substring(0,4);
            }
            priceOfProductsList.add(temp);
        }


        // Сохранение всех продуктов
        for(int i = 0; i < nameOfProductsList.size(); i++){
            try {
                Product product = createProduct(
                        nameOfType,
                        categoryName,
                        nameOfProductsList.get(i),
                        priceOfProductsList.get(i),
                        pathOfPicturesList.get(i));
                if(product.getPathFile().length()>6) {
                    productRepository.save(product);

                    List<List<String>> list = map.get(i);
                    for (List<String> characters : list) {
                        Characteristic characteristic = createCharacteristic(product, characters.get(0), characters.get(1));
                        characteristicRepository.save(characteristic);
                    }
                }
            }catch (IndexOutOfBoundsException e ){
                System.out.println("Какой-то из листов вышел за рамки");
                break;
            }catch(DataIntegrityViolationException ee){
                System.out.println("Повторяющееся имя продукта:" + nameOfProductsList.get(i));
            }


        }

        try {
            System.out.println(++amountOfCategories);
            Thread.sleep(10_000); // Задержка после добавления целой категории в БД (1сек=1000millis)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Переход на следующую страницу
        Element buttonNext = document.select("li[class=page-item page-next]").first();
        if(buttonNext!=null)
            inOneCategory(nameOfType, categoryName, buttonNext.child(0).attr("href"));

    }

    public Type createType(String name){
        Type type = new Type();
        type.setName(name);
        return type;
    }

    public Category createCategory(String nameOfType, String nameOfCategory){
        Category category = new Category();
        category.setName(nameOfCategory);
        category.setTypeId(typeRepository.findByName(nameOfType));
        return category;
    }

    public Product createProduct(String nameOfType,
                               String nameOfCategory,
                               String nameOfProduct,
                               String price,
                               String pathOfPicture){

        Type type = typeRepository.findByName(nameOfType);
        Category category = categoryRepository.findByNameAndTypeId(nameOfCategory, type);

        Product product = new Product();

        product.setTypeId(type);
        product.setCategoryId(category);
        product.setName(nameOfProduct);
        product.setPrice(price);

        product.setPathFile(pathOfPicture);
        product.setIsName(false);
        product.setDataOfCreate(System.currentTimeMillis());
        return product;
    }

    public Characteristic createCharacteristic(Product product, String title, String description){
        Characteristic characteristic = new Characteristic();
        characteristic.setProduct(product);
        characteristic.setTitle(title);
        characteristic.setDescription(description);
        return characteristic;
    }

    // Вытаскиваем страницу по href через случайные host и port
    public Document selectDocumentByHref(String href) {

        Document document = null;
        try {
            document = Jsoup.parse(new URL(href), 3000);
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return document;
    }





    String namesOfType = "Смартфоны";
    String firstCategory = "Apple";
    List<String> namesOfProductsOfApple = Arrays.asList("IPhone 12 (белый)", "IPhone 12 (чёрный)", "IPhone 12 Pro Max",
            "IPhone 12 Pro","IPhone 11 (красный)","IPhone 11 (белый)", "IPhone 11 (черный)", "IPhone 11 (жёлтый)",
            "IPhone 12 mini (фиолетовый)","IPhone 12 mini (синий)",
            "IPhone 12 (красный)", "IPhone 12 (зелёный)");
    List<String> pathOfPictureOfApple = Arrays.asList("https://pngimg.com/uploads/iphone_12/small/iphone_12_PNG36.png",
            "https://pngimg.com/uploads/iphone_12/small/iphone_12_PNG37.png", "https://www.cifrus.ru/photos/medium/apple/apple-iphone-12-pro-128gb-grey-a2341-ll-1.jpg",
            "https://www.cifrus.ru/photos/little/apple/apple-iphone-12-pro-128gb-grey-a2341-ll-3.jpg", "https://www.cifrus.ru/photos/little/apple/apple-iphone-11-64gb-red-a2111-3.jpg",
            "https://www.cifrus.ru/photos/little/apple/apple-iphone-11-64gb-white-a2111-3.jpg", "https://www.cifrus.ru/photos/little/apple/apple-iphone-11-64gb-black-a2111-3.jpg",
            "https://www.cifrus.ru/photos/little/apple/apple-iphone-11-64gb-yellow-a2111-3.jpg", "https://www.cifrus.ru/photos/little/apple/apple-iphone-12-mini-64gb-purple-a2176-ll-3.jpg",
            "https://www.cifrus.ru/photos/little/apple/apple-iphone-12-mini-64gb-blue-a2172-ll-3.jpg", "https://pngimg.com/uploads/iphone_12/small/iphone_12_PNG8.png",
            "https://pngimg.com/uploads/iphone_12/small/iphone_12_PNG17.png");


    @Async
    @ApiOperation(value = "Парсинг данных (12 Продуктов для Apple, Смартфоны)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---")
    })
    @GetMapping("/createMiniDB")
    public void createMiniDB(){

        try {
            typeRepository.save(createType(namesOfType));
        } catch (Exception ignored) {}

        try {
            categoryRepository.save(createCategory(namesOfType, firstCategory));
        } catch (Exception ignored) {}

        for(String product: namesOfProductsOfApple){
            try {
                productRepository.save(createProduct(namesOfType, firstCategory, product, String.valueOf((int) (Math.random() * 10000)),
                        pathOfPictureOfApple.get(namesOfProductsOfApple.indexOf(product))));
            } catch (Exception ignored) {}
        }

        System.out.println("All right");
    }

}
