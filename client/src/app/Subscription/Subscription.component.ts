import {Component, OnInit, Inject} from '@angular/core';

import {SubscriptionService} from "./Subscription.service";
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';

import {Subscription} from "./Subscription";
import {FormControl, Validators, FormGroup, FormBuilder} from '@angular/forms';
import {Room} from "../home/room";
import {Machine} from "../home/machine";
import {HomeService} from "../home/home.service";
import {HomeComponent} from "../home/home.component";
import {MatSnackBar} from '@angular/material';


@Component({
  templateUrl: 'Subscription.html',
  styleUrls: ['./Subscription.component.css']
})
export class SubscriptionComponent implements OnInit {


  public subscriptionDisabled: boolean;
  public machineListTitle: string;
  public brokenMachineListTitle: string;
  public rooms: Room[];
  public machines: Machine[];
  public filteredMachines: Machine[];
  public numOfBroken: number;
  public numOfWashers: number;
  public numOfDryers: number;

  public roomVacant: number;
  public roomRunning: number;
  public roomBroken: number;

  public roomId = '';
  public roomName = 'All rooms';
  public inputRoom = 'all';
  public today = new Date();
  public inputDay: number = this.today.getDay() + 1;

  public mapWidth: number;
  public mapHeight: number;
  options: any;
  name:  string;
  data: any;


  constructor(public dialog: MatDialog, public subscription: MatDialog, public subscriptionService: SubscriptionService, public homeService: HomeService, public _snackBar: MatSnackBar) {
    this.subscriptionDisabled = false;
    this.machineListTitle = 'available within all rooms';
    this.brokenMachineListTitle = 'Unavailable machines within all rooms';

  }
  ngOnInit() {
  }
  translateRoomId(roomId: string): string {
    const room = this.rooms.filter(r => r.id === roomId)[0];
    return room.name;
  }

  // confirmEmail(room: string){
  //   this._snackBar.open('Success! You will be notified when a ' + 'MACHINE TYPE' + ' is available within ' + room, '',{
  //     duration: 2000,
  //     horizontalPosition: 'center'
  //   });
  // }


  openSubscription(room_id: string) {
    // tslint:disable-next-line:max-line-length
    const outOfWashers = this.machines.filter(m => m.room_id === room_id && m.status === 'normal' && m.type === 'washer' && !m.running).length === 0;
    // tslint:disable-next-line:max-line-length
    const outOfDryers = this.machines.filter(m => m.room_id === room_id && m.status === 'normal' && m.type === 'dryer' && !m.running).length === 0;
    const newSub: Subscription = {email: '', type: '', room_id: room_id};
    const dialogRef = this.subscription.open(SubscriptionDialog, {
      width: '500px',
      data: {
        subscription: newSub,
        noWasher: outOfWashers,
        noDryer: outOfDryers,
        roomName: this.translateRoomId(this.roomId)
      },
    });

    dialogRef.afterClosed().subscribe(newSub => {
      if (newSub != null) {
        console.log(newSub);
        this.subscriptionService.addNewSubscription(newSub).subscribe(
          () => {
            this.rooms.filter(m => m.id === this.roomId)[0].isSubscribed = true;
            this.updateRoom(this.roomId, this.roomName);
          },
          err => {
            // This should probably be turned into some sort of meaningful response.
            console.log('There was an error adding the subscription.');
            console.log('The newSub or dialogResult was ' + newSub);
            console.log('The error was ' + JSON.stringify(err));
          }
        );
      }
    });
  }
  public updateRoom(newId: string, newName: string): void {
    this.roomId = newId;
    this.roomName = newName;
    this.machineListTitle = 'available within ' + this.roomName;
    this.brokenMachineListTitle = 'Unavailable machines within ' + this.roomName;
    if (newId === '') {
      this.inputRoom = 'all';
    } else {
      this.inputRoom = newId;
    }
    this.inputDay = this.today.getDay() + 1;
    this.updateMachines();
    this.rooms.map(r => {
      if (r.isSubscribed === undefined) {
        r.isSubscribed = false;
      }
    });
    this.roomVacant = this.filteredMachines.filter(m => m.running === false && m.status === 'normal').length;
    this.roomRunning = this.filteredMachines.filter(m => m.running === true && m.status === 'normal').length;
    this.roomBroken = this.filteredMachines.filter(m => m.status === 'broken').length;
    if (this.roomId !== undefined && this.roomId !== '') {
      // tslint:disable-next-line:max-line-length
      const washerVacant = this.machines.filter(m => m.room_id === this.roomId && m.type === 'washer' && m.status === 'normal' && m.running === false).length;
      // tslint:disable-next-line:max-line-length
      const dryerVacant = this.machines.filter(m => m.room_id === this.roomId && m.type === 'dryer' && m.status === 'normal' && m.running === false).length;
      this.subscriptionDisabled = this.rooms.filter(r => r.id === this.roomId)[0].isSubscribed || (washerVacant !== 0 && dryerVacant !== 0);
    }
  }
  private updateMachines(): void {
    // console.log(this.inputRoom);
    if (this.roomId == null || this.roomId === '') {
      this.filteredMachines = this.machines;
    } else {
      this.filteredMachines = this.machines.filter(machine => machine.room_id === this.roomId);
    }
    this.homeService.updateRunningStatus(this.filteredMachines, this.machines);
    if (this.filteredMachines !== undefined) {
      this.numOfBroken = this.filteredMachines.filter(m => m.status === 'broken').length;
      this.numOfWashers = this.filteredMachines.filter(m => m.status === 'normal' && m.type === 'washer').length;
      this.numOfDryers = this.filteredMachines.filter(m => m.status === 'normal' && m.type === 'dryer').length;
      this.mapHeight = this.filteredMachines.reduce((max, b) => Math.max(max, b.position.y), this.filteredMachines[0].position.y);
      this.mapWidth = this.filteredMachines.reduce((max, b) => Math.max(max, b.position.x), this.filteredMachines[0].position.x);
    }
  }
}



@Component({
  templateUrl: 'Subscription.dialog.html',
})
export class SubscriptionDialog{

  options: FormGroup;
  addSubForm: FormGroup;
  name: string;
  outOfWashers: boolean;
  outOfDryers: boolean;

  constructor(
    public dialogRef: MatDialogRef<SubscriptionDialog>,
    // tslint:disable-next-line:max-line-length
    @Inject(MAT_DIALOG_DATA) public data: { subscription: Subscription, noWasher: boolean, noDryer: boolean, roomName: string }, private fb: FormBuilder) {

    // @ts-ignore
    this.outOfWashers = data.noWasher;
    // @ts-ignore
    this.outOfDryers = data.noDryer;
    // @ts-ignore
    this.name = data.roomName;

    if (this.outOfWashers) {
      data.subscription.type = 'washer';
    } else {
      data.subscription.type = 'dryer';
    }
    // data.subscription.type = 'dryer';

    this.options = fb.group({
      type: data.subscription.type,
    });

    this.ngOnInit();
  }

  add_sub_validation_messages = {
    'email': [
      {type: 'email', message: 'Email must be formatted properly'}
    ]
  };

  createForms() {

    // add user form validations
    this.addSubForm = this.fb.group({
      // We don't need a special validator just for our app here, but there is a default one for email.
      email: new FormControl('email', Validators.email)
    });

    console.log(this.addSubForm);
  }

  // tslint:disable-next-line:use-lifecycle-interface
  ngOnInit() {
    this.createForms();
  }
}
