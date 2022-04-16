package com.example.demo.Service;

import com.example.demo.Controller.AuxiliaryClasses.StaticMethods;
import com.example.demo.DTO.ProductDTO;
import com.example.demo.Entity.Category;
import com.example.demo.Entity.Comparators.ProductPriceComparator;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Characteristic;
import com.example.demo.Entity.Response.ProductWIthNecessaryParameters;
import com.example.demo.Entity.Type;
import com.example.demo.Repositories.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {


    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryService categoryService;
    @Autowired
    TypeService typeService;
    @Autowired
    CharacteristicService characteristicService;



    /**
     * Добавление Продукта
     *
     * @param categoryName название Категорим, к которому будет относится Продукт
     * @param typeName название Типа, к которому будет относится Продукт
     * @param file картинка в битовом представление (ава Продукта)
     * @param ref ссылка на картинку (ава Продукта)
     * @param name название Продукта
     * @param price цена Продукта
     * @param list лист с характеристиками Продукта (Characteristic)
     *
     * @code 201 - Created
     * @code 400 - Incorrect JSON
     * @code 432 - This name of Product already exists
     * @code 433 - This Type doesn't exist
     * @code 434 - This CategoryName (%s) of this Type (%s) doesn't exist
     * @code 435 - Incorrect image extension
     */
    public void addProduct(String categoryName,
                          String typeName,
                          MultipartFile file,
                          String ref,
                          String name,
                          String price,
                          JSONArray list,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        Product product = new Product();

        createProductAndSaveInDB(product, categoryName, typeName, file, ref, name, price, request, response);
        createProductInfoAndSaveInDB(list, product, request, response);
    }

    /**
     * Сохранения Продукта в БД с добавлением ему переданных значений
     * @param product Продукт, которому добавляют все значения
     * @param categoryName название Категорим, к которому будет относится Продукт
     * @param typeName название Типа, к которому будет относится Продукт
     * @param file картинка в битовом представление (ава Продукта)
     * @param ref ссылка на картинку (ава Продукта)
     * @param name название Продукта
     * @param price цена Продукта
     *
     * @code 432 - This name of Product already exists
     * @code 433 - This Type doesn't exist
     * @code 434 - This CategoryName (%s) of this Type (%s) doesn't exist
     * @code 435 - Incorrect image extension
     */
    public void createProductAndSaveInDB(Product product,
                                        String categoryName,
                                        String typeName,
                                        MultipartFile file,
                                        String ref,
                                        String name,
                                        String price,
                                        HttpServletRequest request,
                                        HttpServletResponse response){
        if(productRepository.existsByName(name)){
            Product temp = productRepository.findById(product.getId()).orElse(null);
            if(temp == null || !temp.getName().equals(name)){
                StaticMethods.createResponse(
                        request, response,
                        432, "This name of Product already exists");
                return;
            }

        }
        Type type = typeService.findByName(typeName);
        if(type == null){
            StaticMethods.createResponse(
                    request, response,
                    433, "This Type doesn't exist");
            return;
        }
        Category category = categoryService.findByNameAndTypeId(categoryName, type);
        if(category == null){
            StaticMethods.createResponse(request, response,
                    434,
                    String.format(
                            "This Category (%s) of this Type (%s) doesn't exist",
                            typeName,
                            categoryName));
            return;
        }



        if(file != null) {
            String startName = StaticMethods.getFileExtension(file.getOriginalFilename());
            if(startName==null || (
                    !startName.equals("jpeg")
                            && !startName.equals("jpg")
                            && !startName.equals("pjpeg")
                            && !startName.equals("png")
                            && !startName.equals("tiff")
                            && !startName.equals("wbmp")
                            && !startName.equals("webp"))
            ){
                StaticMethods.createResponse(
                        request,
                        response,
                        435,
                        "Incorrect image extension");
                return;
            }

            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            String pathFile = System.getProperty("user.dir").replace("\\","/") + "/src/main/resources/static/images/" + fileName;


            try {
                file.transferTo(new File(pathFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            product.setPathFile(fileName);
            product.setIsName(true);
        }else {
            product.setPathFile(ref);
            product.setIsName(false);
        }

        product.setTypeId(type);
        product.setCategoryId(category);
        product.setName(name);
        product.setPrice(price);
        product.setDataOfCreate(System.currentTimeMillis());

        productRepository.save(product);
    }

    /**
     * Добавление дополнительно информации Продукту с последующим сохранением в БД
     * @param list лист с характеристиками Продукта (Characteristic)
     * @param product сам Продукт, которому добавляют Characteristic
     *
     * @code 201 - Created
     * @code 400 - Incorrect JSON
     */
    public void createProductInfoAndSaveInDB(JSONArray list,
                                            Product product,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
        try {

            for(int i = 0; i < list.length(); i++){
                String oneInfo = list.getString(i);
                Characteristic characteristic = null;
                try {
                    characteristic = new ObjectMapper().readValue(oneInfo, Characteristic.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                characteristic.setProduct(product);
                characteristicService.addCharacteristic(characteristic);
            }

            StaticMethods.createResponse(request, response, HttpServletResponse.SC_CREATED, "Created");

        } catch (JSONException e) {
            StaticMethods.createResponse(request, response, HttpServletResponse.SC_BAD_REQUEST, "Incorrect JSON");
            e.printStackTrace();
        }
    }


    /**
     * Получение Продуктов исходя из входных параметров
     * @param type Название Типа, к которому относится Продукт
     * @param categories Название Категорим, к которому относится Продукт
     * @param page Номер страницы, которую хочет видеть клиент
     * @param limit Количество Продуктов, отображаемых на одной странице у клиента
     * @param minPrice минимальная цена Продукта
     * @param maxPrice максимальная цена Продукта
     *
     * @code = 432 - The minPrice more than the maxPrice
     * @code = 433 - Products with this type doesn't exists
     * @code = 434 - Products with this categories doesn't exists
     * @code = 435 - Products with this price doesn't exists
     * @code = 436 - Incorrect data of page or limit
     */
    public ProductWIthNecessaryParameters getByParams(String type,
                                           List<String> categories,
                                           int page, int limit,
                                           int minPrice, int maxPrice,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {

        if(maxPrice!=-1 && minPrice > maxPrice){
            StaticMethods.createResponse(
                    request, response,432,"The minPrice more than the maxPrice");
            return null;
        }

        List<Product> list = new ArrayList<>();

        // Условие для получения листа Продуктов исходя из переданных Категорий(если они имеются) и Типа
        if(categories==null || categories.size()==0){
             list = productRepository.findAllByTypeId(typeService.findByName(type));

            if(list.isEmpty()){
                StaticMethods.createResponse(
                        request, response,433,"Products with this type doesn't exists");
                return null;
            }
        }else {
            categories = categories.stream().distinct().collect(Collectors.toList());
            for(String category: categories) {

                Type typeParent = typeService.findByName(type);
                Category categoryParent = categoryService.findByNameAndTypeId(category, typeParent);
                list.addAll(productRepository.findAllByTypeIdAndCategoryId(typeParent, categoryParent));
            }

            if(list.isEmpty()){
                StaticMethods.createResponse(
                        request, response,434,"Products with this categories doesn't exists");
                return null;
            }
        }


        // Продукт с максимальной ценой в этом листе
        Optional<Product> productWithMaxPrice = list.stream().max(new ProductPriceComparator());

        // Продукт с минимальной ценой в этом листе
        Optional<Product> productWithMinPrice = list.stream().min(new ProductPriceComparator());

        list = selectionByPrice(list, minPrice, maxPrice);

        if(list.isEmpty()){
            StaticMethods.createResponse(
                    request, response,435,"Products with this price doesn't exists");
            return null;
        }

        // Сортировка по дате создания
        list.sort((o1, o2) -> o2.getDataOfCreate().compareTo(o1.getDataOfCreate()));

        int amountOfAllProducts = list.size();

        // Определение границ эл. в листе исходя из количества отображаемых эл. на стр. и номера стр.
        int fromIndex = (page - 1) * limit;
        int toIndex = page * limit;
        if(fromIndex >= list.size()){
            StaticMethods.createResponse(
                    request, response,436,"Incorrect data of page or limit");
            return null;
        }
        if(toIndex > list.size()){
            toIndex = list.size();
        }

        List<ProductDTO> listDTO = ProductDTO.createList(list.subList(fromIndex, toIndex));

        return new ProductWIthNecessaryParameters(listDTO,
                amountOfAllProducts,
                productWithMaxPrice.orElse(null).getPrice(),
                productWithMinPrice.orElse(null).getPrice());
    }


    /**
     * Фильтрация листа с Продуктами по цене
     * @param list лист Продуктов, который будет отфильтрован, исходя из мин. и макс. ценовых значений
     * @param minPrice минимальная цена Продукта
     * @param maxPrice максимальная цена Продукта
     */
    public List<Product> selectionByPrice(List<Product> list, int minPrice, int maxPrice){

        if(maxPrice == -1){

            return list.
                    stream().
                    filter(product -> minPrice <= Integer.parseInt(product.getPrice())).
                    collect(Collectors.toList());
        }else{

            return list.
                    stream().
                    filter(product -> minPrice <= Integer.parseInt(product.getPrice())
                            && Integer.parseInt(product.getPrice()) <= maxPrice).
                    collect(Collectors.toList());
        }
    }


    /**
     * Поиск всех Продуктов по Типу
     * @param type Тип, к которому принадлежит Продукт
     */
    public List<Product> findAllByTypeId(Type type){
        return productRepository.findAllByTypeId(type);
    }


    /**
     * Поиск всех Продуктов по Категории
     * @param category Категория, к которой принадлежит Продукт
     */
    public List<Product> findAllByCategoryId(Category category){
        return productRepository.findAllByCategoryId(category);
    }


    /** Получение всех Продуктов*/
    public List<Product> getAll() {
        return productRepository.findAll();
    }


    /**
     * Удаление Продукта по его :id
     * @param body (json) :id Продукта
     *
     * @code 204 - No Content
     * @code 400 - Incorrect JSON
     * @code 432 - There isn't exist Product with this id
     */
    public void deleteProduct(String body, HttpServletRequest request, HttpServletResponse response) {

        String id = StaticMethods.parsingJson(body, "id", request, response);
        if(id == null)
            return;
        Product product = productRepository.findById(Long.valueOf(id)).orElse(null);

        if(product!=null && product.getIsName()){
            new File(System.getProperty("user.dir").replace("\\","/") + "/src/main/resources/static/images/" + product.getPathFile()).delete();
            productRepository.delete(product);
            StaticMethods.createResponse(request, response,
                    HttpServletResponse.SC_NO_CONTENT, "No Content");
            return;
        }

        StaticMethods.createResponse(
                request, response,432,"There isn't exist Product with this id");
    }


    /**
     * Получение Продукта и дальнейшее его конвертирование в ProductDTO
     * @param id :id Продукта
     * @code 432 - There isn't exist Product with this id
     */
    public ProductDTO getDTOById(String id, HttpServletRequest request, HttpServletResponse response) {
        Product product = productRepository.findById(Long.valueOf(id)).orElse(null);
        if(product==null){
            StaticMethods.createResponse(
                    request, response,432,
                    "There isn't exist Product with this id");
            return null;
        }
        return ProductDTO.create(product);
    }

    /**
     * Получение Продукта по :id
     * @param id :id Продукта
     * @code 432 - There isn't exist Product with this id
     */
    public Product getById(String id, HttpServletRequest request, HttpServletResponse response){
        Product product = productRepository.findById(Long.valueOf(id)).orElse(null);
        if(product==null){
            StaticMethods.createResponse(
                    request, response,432,
                    "There isn't exist Product with this id");
            return null;
        }
        return product;
    }


    /**
     * Метод для изменение Продукта по :id
     * @param id :id существующего Продукта, который желаем изменить
     * @param category название Категории, к которой будет относится Продукт
     * @param type название Типа, к которому будет относится Продукт
     * @param file картинка в битовом представление (ава Продукта)
     * @param ref ссылка на картинку (ава Продукта)
     * @param name название Продукта
     * @param price цена Продукта
     *
     * @code 201 - Created
     * @code 400 - Incorrect JSON
     * @code 432 - This name of Product already exists
     * @code 433 - This Type doesn't exist
     * @code 434 - This Category (%s) of this Type (%s) doesn't exist
     * @code 435 - Incorrect image extension
     * @code 436 - Product with this :id doesn't exist
     */
    public void editProduct(String id,
                           String category,
                           String type,
                           String ref,
                           MultipartFile file,
                           String name,
                           String price,
                           JSONArray list,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        Product product = productRepository.findById(Long.valueOf(id)).orElse(null);
        if(product == null){
            StaticMethods.createResponse(request, response, 436, "Product with this :id doesn't exist");
            return;
        }

        createProductAndSaveInDB(product, category, type, file, ref, name, price, request, response);
        createProductInfoAndSaveInDB(list, product, request, response);


    }

    public List<ProductDTO> getTopProducts() {
        List<Product> list = productRepository.findAll();
        list.sort((o1, o2) -> o2.getDataOfCreate().compareTo(o1.getDataOfCreate()));
        list = list.subList(0, Math.min(list.size(), 24));
        return ProductDTO.createList(list);
    }
}
