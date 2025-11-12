import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EventService } from '../../services/event.service';
import { Event } from '../../model/event';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-dialog',
  standalone: true,
  imports: [MatDialogModule,MatFormFieldModule,MatInputModule,MatButtonModule,FormsModule,MatSnackBarModule,CommonModule],
  templateUrl: './book-dialog.component.html',
  styleUrl: './book-dialog.component.css'
})
export class BookDialogComponent {

  ticketCount = 1;
  loading = false;
  eventObjById !: Event;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data : {eventId : number}, 
    public dialogRef: MatDialogRef<BookDialogComponent>,
    private eventService : EventService,
    private snack: MatSnackBar){}

  ngOnInit() {
    this.loadEventDetails();
  }

  loadEventDetails() {
    this.loading = true;
    this.eventService.getEventById(this.data.eventId).subscribe({
      next : (res:any) => {
            let data = res["data"];
            this.eventObjById = data;
            this.loading = false;
      },
      error : () => {
          this.loading = false;
          this.snack.open("Failed to load event details", "Close", {duration :  3000});
          this.dialogRef.close();
      }
    });
  }  

    bookTicket(){
      if (!this.ticketCount || this.ticketCount <= 0 || !this.eventObjById) return;
      this.loading = true;
      this.eventService.bookEvent(this.eventObjById.id, this.ticketCount).subscribe({
        next :() => {
          this.loading = false;
          this.snack.open("Event Booked Successfully", "Ok", {duration: 2000});
          this.dialogRef.close('booked');
        },
        error: (err) => {
          this.loading = false;
          const msg = err?.error?.message || (err?.status === 409 ? 'Not enough tickets' : 'Booking failed');
          this.snack.open(msg, 'Close', { duration: 3000 });
        }
      });
    }
}
