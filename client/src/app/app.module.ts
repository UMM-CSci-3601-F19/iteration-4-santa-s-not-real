import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {HttpClientModule, HttpClient} from '@angular/common/http';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {HomeService} from './home/home.service';
import {Routing} from './app.routes';
import {APP_BASE_HREF} from '@angular/common';

import {CustomModule} from './custom.module';

import {HomeDialog} from './home/home.component';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatSelectModule} from '@angular/material/select';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatProgressBarModule, MatRadioModule} from '@angular/material';
import {AuthService} from './auth.service';
import {SubscriptionComponent} from "./Subscription/Subscription.component";
import {SubscriptionDialog} from "./Subscription/Subscription.component";


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
    MatRadioModule,
    // MDCRipple,
    // ScrollDispatchModule,
    // CdkStepperModule,
  ],
  declarations: [
    AppComponent,
    HomeComponent,
    HomeDialog,
    SubscriptionComponent,
    SubscriptionDialog

  ],
  providers: [
    HttpClient,
    HomeService,
    AuthService,
    {provide: APP_BASE_HREF, useValue: '/'},
  ],
  entryComponents: [
    HomeDialog,
    SubscriptionDialog
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
}
