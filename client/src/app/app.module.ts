import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {HttpClientModule, HttpClient} from '@angular/common/http';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {RoomDialogPage} from "./home/home.component";
import {HomeService} from './home/home.service';
import {Routing} from './app.routes';
import {APP_BASE_HREF} from '@angular/common';
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogConfig} from "@angular/material/dialog";

import {CustomModule} from './custom.module';

import {HomeMachineDialog} from './home/home.component';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatSelectModule} from '@angular/material/select';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatProgressBarModule} from '@angular/material';


// import {MDCRipple} from '@material/ripple';

// import {ScrollDispatchModule} from '@angular/cdk/scrolling';
// import {CdkStepperModule} from '@angular/cdk/stepper';
// import {CdkTableModule} from '@angular/cdk/table';
// import {CdkTreeModule} from '@angular/cdk/tree';

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    Routing,
    CustomModule,
    MatGridListModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    // MDCRipple,
    // ScrollDispatchModule,
    // CdkStepperModule,
  ],
  declarations: [
    AppComponent,
    HomeComponent,
    HomeMachineDialog,
    RoomDialogPage,
  ],
  providers: [
    HttpClient,
    HomeService,
    {provide: APP_BASE_HREF, useValue: '/'},
    {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: false}},
  ],
  entryComponents: [
    HomeMachineDialog,
    RoomDialogPage,
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
}
