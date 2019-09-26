package kz.app.controllers.v1;

import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Produces;
import kz.app.data.MessageDto;
import kz.app.exceptions.BadRequestException;
import kz.app.exceptions.MethodNotAllowedException;
import kz.app.exceptions.NotFoundException;

@Controller
public class ErrorController {
    @Error(exception = BadRequestException.class, global = true)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<MessageDto> badRequest(HttpRequest request, BadRequestException ex) {
        return HttpResponse.badRequest(new MessageDto(ex.getMsg()));
    }

    @Error(exception = NotFoundException.class, global = true)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<MessageDto> notFound(HttpRequest request, NotFoundException ex) {
        return HttpResponse.badRequest(new MessageDto(ex.getMessage()));
    }

    @Error(exception = MethodNotAllowedException.class, global = true)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<MessageDto> notAllowed(HttpRequest request, MethodNotAllowedException ex) {
        return HttpResponse.notAllowed(HttpMethod.POST);
    }
}
