/** Classe représentant un fichier */
export class FileSystem {
  public name: string  = '';
  public path: string  = '';
  public size: number  = 0.0;
  public lastModified: number  = 0.0;
  public user: string  = '';
  public group: string  = '';
  public userPermission: string  = '';
  public groupPermission: string  = '';
  public otherPermission: string  = '';
  public pathParent: string  = '';
  public directory: boolean;
}
