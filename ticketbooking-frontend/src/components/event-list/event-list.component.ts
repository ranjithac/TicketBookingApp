import { Component, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { Event } from '../../model/event';
import { EventService } from '../../services/event.service';
import { MatDialog } from '@angular/material/dialog';
import { BookDialogComponent } from '../book-dialog/book-dialog.component';


@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [MatTableModule],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.css'
})
export class EventListComponent implements OnInit{

  constructor(private eventService : EventService, private dialog : MatDialog){}

  events: Event[] = [];
  columns = ['name', 'remainingTickets', 'action'];

  ngOnInit(): void {
    this.load();
  }

  openBooking(event: Event) {
    console.log("open booking dialog box");
    const ref = this.dialog.open(BookDialogComponent, 
    {
      data : {eventId: event.id}
    });
    ref.afterClosed().subscribe(result => {
      if(result == 'booked'){
        this.load();
      }
    })
  }

  load(){
    this.eventService.listEvents().subscribe(data => this.events = data);
  }
}
