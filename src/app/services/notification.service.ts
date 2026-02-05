import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/notification';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private apiUrl = 'http://localhost:8082/api/notifications';

    constructor(private http: HttpClient) { }

    getNotifications(customerId: number): Observable<Notification[]> {
        return this.http.get<Notification[]>(`${this.apiUrl}/${customerId}`);
    }
}
