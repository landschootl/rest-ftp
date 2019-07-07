import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FileSystem} from "./beans/FileSystem";
import * as FileSaver from "file-saver";
import {FileSystemService} from "./file-system.service";
import {NotifyUtils, TypeNotify} from "./utils/notifyUtils";
declare var $: any;

@Component({
  selector: 'app-file-system',
  templateUrl: './file-system.component.html',
  styleUrls: ['./file-system.component.css']
})
export class FileSystemComponent implements OnInit {

  private _paths: string[] = [];

  @ViewChild('createFolderModal') createFolderModalRef: ElementRef;
  @ViewChild('nameCreateFolderInput') nameCreateFolderInputRef: ElementRef;
  public errorCreateFolder: string = null;

  @ViewChild('updateFileModal') updateFileModalRef: ElementRef;
  @ViewChild('nameUpdateFileInput') nameUpdateFileInputRef: ElementRef;
  public fileToUpdate = new FileSystem();

  @ViewChild('fileUploadInput') fileUploadInputRef: ElementRef;
  public filesToUpload;

  public searchRecursiveActived = false;
  public searchChaineFilter = "";

  public fileMemory: FileSystem = null;

  constructor(private _fileSystemService: FileSystemService) { }

  ngOnInit() {
    this.searchFiles();
    NotifyUtils.notif("Bienvenue sur mon projet, bonne visiste !\nLudovic Landschoot", TypeNotify.INFO);
    NotifyUtils.notif("Un README.md est disponible à la racine du projet pour des informations sur le tp..", TypeNotify.INFO);
  }

  public getPathString(): string {
    var path: string = "";
    this._paths.forEach(element => path = path + "/" + element);
    return path;
  }

  public clickFile(file: FileSystem): void {
    if(file.directory){
      var pathString: string = file.path.replace(this.getPathString(), "");
      var paths: string[] = pathString.split("/");
      for(var name of paths.slice(1, paths.length)){
        this._paths.push(name);
      }
      this.searchFiles();
    }
  }

  public moveToParentDirectory(): void {
    this._paths.pop();
    this.searchFiles();
  }

  public changePositionPath(index: number): void {
    this._paths = this._paths.slice(0, index+1);
    this.searchFiles();
  }

  public searchFiles(): void {
    this._fileSystemService.getFilesWithFilter(this.getPathString(), this.searchChaineFilter, this.searchRecursiveActived);
  }

  public changeSearchRecursive(): void {
    this.searchRecursiveActived = !this.searchRecursiveActived;
    this.searchFiles()
  }

  public createFolder(name: string): void {
    if(name.length>0) {
      this.errorCreateFolder = null;
      var newFolder: FileSystem = Object.assign(new FileSystem(), {
        "directory": true,
        "name": name
      });
      this._fileSystemService.createFolder(this.getPathString(), newFolder)
        .subscribe(
          folder => {
            $(this.createFolderModalRef.nativeElement).modal('hide');
            this.nameCreateFolderInputRef.nativeElement.value = "";
            NotifyUtils.notif("Dossier créé avec succès", TypeNotify.SUCCESS);
          },
          error => NotifyUtils.notif(error.json().message, TypeNotify.ERROR)
        );
    } else {
      NotifyUtils.notif("Attention, le nom ne doit pas être vide", TypeNotify.WARNING);
    }
  }

  public selectFileToUpdate(file: FileSystem): void {
    this.fileToUpdate = file;
    $(this.updateFileModalRef.nativeElement).modal('show');
  }

  public updateFile(): void {
    if(this.fileToUpdate.name.length>0) {
      this._fileSystemService.updateFile(this.fileToUpdate)
        .subscribe(
          file => {
            this.searchChaineFilter = "";
            this.searchFiles();
            $(this.updateFileModalRef.nativeElement).modal('hide');
            NotifyUtils.notif("Fichier modifié avec succès", TypeNotify.SUCCESS);
          },
          error => NotifyUtils.notif(error.json().message, TypeNotify.ERROR)
        );
    } else {
      NotifyUtils.notif("Attention, le nom ne doit pas être vide", TypeNotify.WARNING);
    }
  }

  public deleteFile(file: FileSystem): void {
    if(confirm("Etes-vous certain de vouloir supprimer le fichier ?")) {
      if(file === this.fileMemory){
        this.fileMemory = null;
      }
      this._fileSystemService.deleteFile(file.path)
        .subscribe(
          () => {
            NotifyUtils.notif("Fichier supprimé avec succès", TypeNotify.SUCCESS);
          },
          error => NotifyUtils.notif(error.json().message, TypeNotify.ERROR)
        );
    }
  }

  public prepareUpload($event): void {
    this.filesToUpload = $event.srcElement.files;
  }

  public uploadFile(): void {
    this._fileSystemService.uploadFile(this.getPathString(), this.filesToUpload);
  }

  public loadFile(file: FileSystem): void {
    this._fileSystemService.loadFile(file)
      .then(res => {
        const data = new Blob([res.blob()]);
        FileSaver.saveAs(data, file.name);
        NotifyUtils.notif("fichier récupéré avec succès", TypeNotify.SUCCESS);
      });
  }

  public copyFile(file: FileSystem): void {
    this.fileMemory = file;
  }

  public pasteFile(): void {
    this._fileSystemService.pasteFile(this.getPathString(), this.fileMemory)
      .subscribe(
        () => {
          NotifyUtils.notif("Fichier collé avec succès", TypeNotify.SUCCESS);
        },
        error => NotifyUtils.notif(error.json().message, TypeNotify.ERROR)
      );
  }
}
