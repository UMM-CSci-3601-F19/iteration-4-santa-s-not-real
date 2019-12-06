// Imports
import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {HomeComponent} from './home/home.component';

// Route Configuration
export const routes: Routes = [
  {path: '', component: HomeComponent},
];

export const Routing: ModuleWithProviders = RouterModule.forRoot(routes);
