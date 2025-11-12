import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EventListComponent } from '../components/event-list/event-list.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';


@Component({
  selector: 'app-root',
  standalone : true,
  imports: [MatToolbarModule, EventListComponent,MatIconModule,MatCardModule,MatTableModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

}
