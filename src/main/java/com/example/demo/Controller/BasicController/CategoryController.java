package com.example.demo.Controller.BasicController;

import com.example.demo.Service.CategoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "Category")
@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "Добавление новой категории")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 432, message = "Such Category of this Type already exist"),
            @ApiResponse(code = 433, message = "Such Type doesn't exist")
    })
    @PostMapping("/add")
    public void addCategory(
            @ApiParam(value = "Название категории и название типа",
                      example = "{\n\"name\":\"Apple\",\n\"type\":\"Смартфоны\"\n}",
                      required = true)
            @RequestBody String body,
            HttpServletRequest request,
            HttpServletResponse response)  {
        categoryService.addCategory(body, request, response);
    }


    @ApiOperation(value = "Удаление категории")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 432, message = "There isn't exist Category with this id")
    })
    @DeleteMapping("/delete")
    public void deleteCategory(
            @ApiParam(value = ":id категории, которую мы хотим удалить",
                      example = "{\n\"id\":\"5\"\n}",
                      required = true)
            @RequestBody String body,
            HttpServletRequest request,
            HttpServletResponse response){
        categoryService.deleteCategory(body, request, response);
    }

    @ApiOperation(value = "Редактирование категории")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 432, message = "There isn't exist Category with this id")
    })
    @PutMapping("/edit")
    public void editCategory(
            @ApiParam(value = ":id категории, который мы хотим редактировать. Новое имя",
                      example = "{\n\"id\":\"5\"\n, \n\"name\":\"Apple\"\n}",
                      required = true)
            @RequestBody String body,
            HttpServletRequest request,
            HttpServletResponse response){
        categoryService.editCategory(body, request, response);
    }

}
