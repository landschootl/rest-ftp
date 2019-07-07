import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {Http, Headers, RequestOptions, ResponseContentType} from "@angular/http";
import {FileSystem} from "./beans/FileSystem";
import {FileUploader} from "ng2-file-upload";
import {NotifyUtils, TypeNotify} from "./utils/notifyUtils";

@Injectable()
export class FileSystemService {

  public allFiles$: BehaviorSubject<FileSystem[]> = new BehaviorSubject<FileSystem[]>([]);
  private _allFileList: FileSystem[] = [];

  public uploader: FileUploader = new FileUploader({url: "/api/file"});

  constructor(
    private _http: Http
  ) {}

  public getFilesWithFilter(path: string, chaine: string, searchRecursiveActived: boolean): void {
    this._http.get(`/api/directories${path}?chaine=${chaine}&recursif=${searchRecursiveActived}`)
      .map(res => res.json())
      .subscribe(
        filesList => {
          this._allFileList = filesList.map(current => Object.assign(new FileSystem(), current));
          this.allFiles$.next(this._allFileList);
        },
        error => console.log('Erreur getAllFiles', error)
      );
  }

  public createFolder(path: string, folder: FileSystem): Observable<FileSystem> {
    const body = JSON.stringify(folder);
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    return this._http.post(`/api/directory${path}`, body, {
      headers: headers
    })
      .map(res => res.json())
      .do(res => {
        const file = Object.assign(new FileSystem(), res);
        this._allFileList = [...this._allFileList, file];
        this.allFiles$.next(this._allFileList);
      });
  }

  public updateFile(file: FileSystem): Observable<FileSystem> {
    const body = JSON.stringify(file);
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    return this._http.put(`/api/file${file.path}`, body, {
      headers: headers
    })
      .map(res => res.json())
      .do(res => {
        for(var i=0; i<this._allFileList.length; i++){
          if(this._allFileList[i].path === file.path){
            this._allFileList[i] = Object.assign(new FileSystem(), res);
          }
        }
        this.allFiles$.next(this._allFileList);
      });
  }

  public deleteFile(path: string): Observable<any> {
    return this._http.delete(`/api/file${path}`)
      .do(() => {
        this._allFileList = this._allFileList.filter(file => file.path!==path);
        this.allFiles$.next(this._allFileList);
      })
  }

  public uploadFile(pathString: string, fileToUpdate) {
    this.uploader.options.url = "/api/file"+pathString;
    this.uploader.addToQueue(fileToUpdate);
    this.uploader.uploadAll();
    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      if (status === 200) {
        const file = JSON.parse(response) as FileSystem;
        const newFile = Object.assign(new FileSystem(), file);
        this._allFileList = [...this._allFileList, newFile];
        this.allFiles$.next(this._allFileList);
        NotifyUtils.notif("Le fichier est bien enregistré", TypeNotify.SUCCESS);
      } else {
        NotifyUtils.notif(JSON.parse(response).message, TypeNotify.ERROR);
      }
    }
  }

  public loadFile(file: FileSystem) {
    return this._http.get(`/api/file${file.path}`, { responseType: ResponseContentType.Blob })
      .toPromise();
  }

  public pasteFile(path: string, file: FileSystem) {
    const body = JSON.stringify(file);
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    return this._http.post(`/api/copy/file${path}`, body, {
      headers: headers
    })
      .map(res => res.json())
      .do(res => {
        const file = Object.assign(new FileSystem(), res);
        this._allFileList = [...this._allFileList, file];
        this.allFiles$.next(this._allFileList);
      });
  }
}
