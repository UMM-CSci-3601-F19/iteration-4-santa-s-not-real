
import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs';

import {Room} from "../home/room";
import {environment} from '../../environments/environment';
import {Subscription} from "./Subscription";


@Injectable()
export class SubscriptionService {
  readonly baseUrl: string = environment.API_URL;
  private roomURL: string = this.baseUrl + 'rooms';
  private machineURL: string = this.baseUrl + 'machines';
  private subURL: string = this.baseUrl + 'subscribe';

  // private historyURL: string = this.baseUrl + 'history';

  constructor(private http: HttpClient) {
  }

  addNewSubscription(newSub: Subscription): Observable<string> {
    const httpOptions = {
      headers: new HttpHeaders({
        // We're sending JSON
        'Content-Type': 'application/json'
      }),
      responseType: 'text' as 'json'
    };

    console.log(httpOptions);
    return this.http.post<string>(this.subURL + '/new', newSub, httpOptions);
  }
}
