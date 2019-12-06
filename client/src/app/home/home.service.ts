import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs';

import {Room} from './room';
import {Machine} from './machine';
import {History} from './history';
import {environment} from '../../environments/environment';
import {Subscription} from './subscription';

@Injectable()
export class HomeService {
  readonly baseUrl: string = environment.API_URL ;
  private roomURL: string = this.baseUrl + 'rooms';
  private machineURL: string = this.baseUrl + 'machines';
  private subURL: string = this.baseUrl + 'subscribe';
  // private historyURL: string = this.baseUrl + 'history';

  constructor(private http: HttpClient) {
  }

  getRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(this.roomURL);
  }
  getMachines(): Observable<Machine[]> {
    return this.http.get<Machine[]>(this.machineURL);
  }


  getAllHistory(): Observable<History[]>{
    return this.http.get<History[]>(this.baseUrl + '/all_history');
  }

  updateAvailableMachineNumber(rooms: Room[], machines: Machine[]): void {
    if (rooms != null) {
      rooms.map(room => {
        room.numberOfAllMachines = machines.filter(machine => machine.room_id === room.id && machine.status === 'normal').length;
        room.numberOfAvailableMachines = machines.filter(machine => machine.room_id === room.id && machine.status === 'normal')
          .filter(machine => machine.running === false).length;
      });
    }
  }

  updateRunningStatus(filteredMachines: Machine[], machines: Machine[]): void {
    if (filteredMachines != null) {
      filteredMachines.map(machine => {
        machine.remainingTime = machines.filter(m => m.id === machine.id)[0].remainingTime;
        machine.vacantTime = machines.filter(m => m.id === machine.id)[0].vacantTime;
        machine.running = machines.filter(m => m.id === machine.id)[0].running;
      });
    }
  }

  addNewSubscription(newSub: Subscription): Observable<string> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      responseType: 'text' as 'json'
    };

    console.log(httpOptions);
    return this.http.post<string>(this.subURL + '/new', newSub, httpOptions);
  }
}

