import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { TreeModule } from "angular-tree-component";

import { AppComponent } from './app.component';
import { FileSystemComponent } from './file-system/file-system.component';
import { FileSystemService } from "./file-system/file-system.service";

@NgModule({
  declarations: [
    AppComponent,
    FileSystemComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    TreeModule
  ],
  providers: [
    FileSystemService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
