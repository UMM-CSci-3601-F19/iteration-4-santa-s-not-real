
import {Component, OnInit, Inject} from '@angular/core';
import {Observable} from 'rxjs';

import {SubscriptionService} from "./Subscription.service";

import * as Chart from 'chart.js';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';

import {Subscription} from "./Subscription";
import {FormControl, Validators, FormGroup, FormBuilder} from '@angular/forms';
import {Room} from "../home/room";
import {Machine} from "../home/machine";
import {HomeService} from "../home/home.service";
import {HomeComponent} from "../home/home.component";
import

@Component({
  templateUrl: 'Subscription.component.html',
  styleUrls: ['./Subscription.component.css']
})
export class HomeComponent implements OnInit {


  public subscriptionDisabled: boolean;

  constructor(public dialog: MatDialog, public subscription: MatDialog, public subscriptionService:SubscriptionService, public rooms:Room) {
    this.subscriptionDisabled = false;
    this.rooms = rooms;

  }


  openSubscription(room_id: string) {
    // tslint:disable-next-line:max-line-length
    const outOfWashers = this.machines.filter(m => m.room_id === room_id && m.status === 'normal' && m.type === 'washer' && !m.running).length === 0;
    // tslint:disable-next-line:max-line-length
    const outOfDryers = this.machines.filter(m => m.room_id === room_id && m.status === 'normal' && m.type === 'dryer' && !m.running).length === 0;
    const newSub: Subscription = {email: '', type: '', room_id: room_id};
    const dialogRef = this.subscription.open(SubscriptionDialog, {
      width: '500px',
      data: {subscription: newSub, noWasher: outOfWashers, noDryer: outOfDryers, roomName: this.translateRoomId(this.roomId)},
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


  @Component({
    templateUrl: 'subscription.service.html',
  })
  export class SubscriptionDialog {

  options: FormGroup;
  addSubForm: FormGroup;
  name: string;
  outOfWashers: boolean;
  outOfDryers: boolean;

  constructor(
    public dialogRef: MatDialogRef<SubscriptionDialog>,
    // tslint:disable-next-line:max-line-length
    @Inject(MAT_DIALOG_DATA) public data: { subscription: Subscription, noWasher: boolean, noDryer: boolean, roomName: string }, private fb: FormBuilder) {

    this.outOfWashers = data.noWasher;
    this.outOfDryers = data.noDryer;
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


    // console.log(this.outOfDryers);
    // console.log(this.outOfWashers);


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
}

