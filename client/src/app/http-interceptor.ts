import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from './services/authentication/authentication.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    constructor(private authenticationService: AuthenticationService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const accessToken: string = this.authenticationService.getAccessToken();
        if (!accessToken) {
            return next.handle(request);
        }

        // We clone the request, because the original request is immutable
        return next.handle(request.clone({
            setHeaders: {
                Authorization: 'Bearer ' + accessToken
            }
        }));
    }
}
