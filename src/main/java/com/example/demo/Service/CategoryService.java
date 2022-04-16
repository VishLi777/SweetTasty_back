package com.example.demo.Service;

import com.example.demo.Controller.AuxiliaryClasses.StaticMethods;
import com.example.demo.Entity.Category;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Type;
import com.example.demo.Repositories.CategoryRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TypeService typeService;
    @Autowired
    ProductService productService;



    /**
     * Добавление новой Категории
     * @param body [json] название Типа(type),
     *             которому принадлежит Категория, и название новой Категории(name)
     * @code 201 - Created
     * @code 400 - Incorrect JSON
     * @code 432 - Such Category of this Type already exist
     */
    public void addCategory(String body, HttpServletRequest request, HttpServletResponse response) {
        Type type = typeService.findByName("Стандарт");

        Category category = new Category();

        try {
            JSONObject obj = new JSONObject(body);
//                Type typeId = typeService.findByName(obj.getString("type"));

            String name = obj.getString("name");

            category.setName(name);
            category.setTypeId(type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Category> list = type.getCategories();
        for(Category currentCategory : list){
            if(currentCategory.getName().equals(category.getName())){
                StaticMethods.createResponse(
                        request, response,432,
                        "Such Category of this Type already exist");
                return;
            }
        }

        category.setTypeId(type);
        categoryRepository.save(category);
        StaticMethods.createResponse(request,response,HttpServletResponse.SC_CREATED, "Created");


    }


    /**
     * Получение Категории по её имени и Типу, к которому он принадлежит
     * @param categoryName наименование Категории
     * @param type Тип, к которому принадлежит Категория
     * @return Надейнная Категория
     */
    public Category findByNameAndTypeId(String categoryName, Type type) {
        return categoryRepository.findByNameAndTypeId(categoryName, type);
    }


    /**
     * Удаление Категории по :id
     * @param body [json] :id Категории, которую необходимо удалить
     * @code 204 - No Content
     * @code 400 - Incorrect JSON
     * @code 432 - There isn't exist Category with this id
     */
    public void deleteCategory(String body, HttpServletRequest request, HttpServletResponse response) {
        String id = StaticMethods.parsingJson(body, "id", request, response);
        if(id == null)
            return;
        Category category = categoryRepository.findById(Long.valueOf(id)).orElse(null);
        if(category !=null) {

            // Поиск всех продуктов для удаления картинок из static/images
            List<Product> list = productService.findAllByCategoryId(category);
            for (Product product: list){
                if(product.getIsName())
                    new File(System.getProperty("user.dir").replace("\\","/") + "/src/main/resources/static/images/" + product.getPathFile()).delete();
            }


            categoryRepository.delete(category);
            StaticMethods.createResponse(request, response, HttpServletResponse.SC_NO_CONTENT, "No Content");
            return;
        }

        StaticMethods.createResponse(
                request, response, 432, "There isn't exist Category with this id");
    }


    /**
     * Изменение существующей Категории по :id
     *
     * @param body [json] :id Категории и её новое наименование (name)
     *
     * @code 201 - Created
     * @code 400 - Incorrect JSON
     * @code 432 - There isn't exist Category with this id
     */
    public void editCategory(String body, HttpServletRequest request, HttpServletResponse response){
        String id = StaticMethods.parsingJson(body, "id", request, response);
        Category category = categoryRepository.findById(Long.valueOf(id)).orElse(null);
        if(category !=null){
            category.setName(StaticMethods.parsingJson(body, "name", request, response));
            categoryRepository.save(category);
            StaticMethods.createResponse(request, response, HttpServletResponse.SC_CREATED, "Created");
        }

        StaticMethods.createResponse(
                request, response, 432, "There isn't exist Category with this id");
    }
}
