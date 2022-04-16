package com.example.demo.Controller.BasicController;

import com.example.demo.DTO.ProductDTO;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Response.ProductWIthNecessaryParameters;
import com.example.demo.Service.ProductService;
import io.swagger.annotations.*;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "Product")
@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @ApiOperation(value = "Добавление продуктов")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Incorrect JSON"),
            @ApiResponse(code = 432, message = "This name of Product already exists"),
            @ApiResponse(code = 433, message = "This Type doesn't exist"),
            @ApiResponse(code = 434, message = "This Category of this Type doesn't exist"),
            @ApiResponse(code = 435, message = "Incorrect image extension")
    })
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void addProduct(
            @ApiParam(
                    name = "Product",
                    value = "Все данные о продукте:\ncategory - Категория, к которой будет принадлежать Продукт" +
                            "\ntype - Тип, к которому будет принадлежать Продукт\nname - название Продукта" +
                            "\nprice - цена Продукта\nОдно из двух(imgFile или imgRef):\nimgFile - файл-фото\n" +
                            "imgRef - путь до изображения из инета\n" +
                            "characteristic - характеристики Продукта\ntitle - титл\ndescription - описание",
                    example = "{{\ncategory: \"\",\ntype: \"\",\nname: \"Iphone 12 pro\", \n" +
                            "price: \"25000\" , \n    imgFile: *Файл*\n        /\n" +
                            "    imgRef: *путь до изображения*\ncharacteristic: [\n" +
                            "     {\n       title: \"\"\n       description: \"\"\n" +
                            "     },\n     {...}\n]\n}}",
                    required = true
            )
            @RequestParam(value = "imgFile", required = false) MultipartFile file,
            @ApiParam(hidden = true)
            @RequestParam("category") String category,
            @ApiParam(hidden = true)
            @RequestParam("type") String type,
            @ApiParam(hidden = true)
            @RequestParam(value = "imgRef", required = false) String ref,
            @ApiParam(hidden = true)
            @RequestParam("name") String name,
            @ApiParam(hidden = true)
            @RequestParam("price") String price,
            @ApiParam(hidden = true)
            @RequestParam("characteristic") JSONArray list,
            HttpServletRequest request,
            HttpServletResponse response){

        productService.addProduct(category, type, file, ref, name, price, list, request, response);

    }


    @ApiOperation(value = "Получение Продуктов по параметрам")
    @ApiResponses(value = {
            @ApiResponse(code = 432, message = "The minPrice more than the maxPrice"),
            @ApiResponse(code = 433, message = "Products with this type doesn't exists"),
            @ApiResponse(code = 434, message = "Products with this categories doesn't exists"),
            @ApiResponse(code = 435, message = "Products with this price doesn't exists"),
            @ApiResponse(code = 436, message = "Incorrect data of page or limit")
    })
    @GetMapping("/getByParams")
    public ProductWIthNecessaryParameters getByParams(
            @ApiParam(
                    value = "Наименование Типа",
                    example = "Смартфоны",
                    required = true
            )
             @RequestParam("type") String type,
            @ApiParam(
                    value = "Наименование Категорий",
                    example = "Apple"
            )
             @RequestParam(value = "category", required = false) List<String> category,
            @ApiParam(
                    value = "Номер страницы",
                    example = "5"
            )
             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @ApiParam(
                    value = "Количество продуктов, которые отображаются на одной странице",
                    example = "50"
            )
             @RequestParam(value = "limit", required = false , defaultValue = "20") int limit,
            @ApiParam(
                    value = "Минимальная цена продукта",
                    example = "1500"
            )
             @RequestParam(value = "minPrice", required = false, defaultValue = "0") int minPrice,
            @ApiParam(
                    value = "Максимальная цена продукта",
                    example = "5000"
            )
             @RequestParam(value = "maxPrice", required = false, defaultValue = "-1") int maxPrice,
                            HttpServletRequest request,
                            HttpServletResponse response){

        return productService.getByParams(type, category, page, limit, minPrice, maxPrice, request, response);
    }


    @ApiOperation(value = "Удаление продукта")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 432, message = "There isn't exist Product with this id")
    })
    @DeleteMapping("/delete")
    public void deleteProduct(
            @ApiParam(
                    value = ":id Продукта, который необходимо удалить",
                    example = "{\n\"id\":\"5\"\n}"
            )
            @RequestBody String body,
            HttpServletRequest request,
            HttpServletResponse response){
        productService.deleteProduct(body, request, response);
    }


    @ApiOperation(value = "Изменение продуктов")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Incorrect JSON"),
            @ApiResponse(code = 432, message = "This name of Product already exists"),
            @ApiResponse(code = 433, message = "This Type doesn't exist"),
            @ApiResponse(code = 434, message = "This Category of this Type doesn't exist"),
            @ApiResponse(code = 435, message = "Incorrect image extension")
    })
    @PutMapping(value = "/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void editProduct(

            @ApiParam(
                    name = "Product",
                    value = "Все данные о продукте:\nid - :id существующего Продукта, который желаем изменить" +
                            "\ncategory - категория, к которому будет принадлежать Продукт" +
                            "\ntype - Тип, к которому будет принадлежать Продукт\nname - название Продукта" +
                            "\nprice - цена Продукта\nОдно из двух(imgFile или imgRef):\nimgFile - файл-фото\n" +
                            "imgRef - путь до изображения из инета\n" +
                            "characteristic - характеристики Продукта\nid - :id характеристики" +
                            "\ntitle - титл\ndescription - описание",
                    example = "{{\nid:\"\"\ncategory: \"\",\ntype: \"\",\nname: \"Iphone 12 pro\", \n" +
                            "price: \"25000\" , \n    imgFile: *Файл*\n        /\n" +
                            "    imgRef: *путь до изображения*\ncharacteristic: [\n" +
                            "     {\n       id: \"\"\n       title: \"\"\n       description: \"\"\n" +
                            "     },\n     {...}\n]\n}}",
                    required = true
            )
            @RequestParam(value = "imgFile", required = false) MultipartFile file,
            @ApiParam(hidden = true)
            @RequestParam("id") String id,
            @ApiParam(hidden = true)
            @RequestParam("category") String category,
            @ApiParam(hidden = true)
            @RequestParam("type") String type,
            @ApiParam(hidden = true)
            @RequestParam(value = "imgRef", required = false) String ref,
            @ApiParam(hidden = true)
            @RequestParam("name") String name,
            @ApiParam(hidden = true)
            @RequestParam("price") String price,
            @ApiParam(hidden = true)
            @RequestParam("characteristic") JSONArray list,
            HttpServletRequest request,
            HttpServletResponse response) {
        productService.editProduct(id, category, type, ref,file, name, price, list, request, response);
    }


    @ApiOperation(value = "Получение продукта по :id")
    @ApiResponses(value = {
            @ApiResponse(code = 432, message = "There isn't exist Product with this id")
    })
    @GetMapping("/getById/{id}")
    public ProductDTO getById(
            @ApiParam(
                    value = ":id Продукта, который необходимо получить",
                    example = "5",
                    required = true
            )
            @PathVariable String id,
            HttpServletRequest request,
            HttpServletResponse response){
        return productService.getDTOById(id, request, response);
    }

    @ApiOperation(value = "Топ 24 новых продуктов")
    @GetMapping("/getTopProducts")
    public List<ProductDTO> getTopProducts(){
        return productService.getTopProducts();
    }

    // For my tests
    @ApiOperation(value = "Получение всех продуктов без обработки от лишних полей", hidden = true)
    @GetMapping("/getAll")
    public List<Product> getAll(){
        return productService.getAll();
    }


}
