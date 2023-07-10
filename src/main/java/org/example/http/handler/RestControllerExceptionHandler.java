package org.example.http.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "org.example.http.rest") //отлавливаем исключения для rest контроллеров
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler { // нужно унаследоваться, для обработки исключение результатом которых являются объекты

}
