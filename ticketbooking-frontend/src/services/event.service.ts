import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Event } from '../model/event';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  
  constructor (private http: HttpClient){}

  listEvents() : Observable<Event[]>{
    return this.http.get<Event[]>('/api/tickets');
  }

  bookEvent(eventId : number, ticketCount : number){
    return this.http.post<Event>(`/api/tickets/${eventId}/book?count=${ticketCount}`,{});
  }

  getEventById(id : number) : Observable<Event>{
    return this.http.get<Event>(`/api/tickets/${id}`);
  }
}
