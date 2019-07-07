/**
 * Created by landschoot on 01/06/17.
 */
/** Classe qui gère les notifications */
const Noty = require('noty');
export class TypeNotify {
  static SUCCESS = "success";
  static ERROR = "error";
  static WARNING = "warning";
  static INFO = "info";
}

export class NotifyUtils {
  public static notif(text: string, type: string, ): void {
    new Noty({
      type: type,
      layout: 'bottomCenter',
      theme: 'sunset',
      text: text,
      timeout: 4500,
      progressBar: true,
      closeWith: ['click', 'button'],
      animation: {
        open: 'noty_effects_open',
        close: 'noty_effects_close'
      },
      id: false,
      force: false,
      killer: false,
      queue: 'global',
      container: false,
      buttons: [],
      sounds: {
        sources: [],
        volume: 1,
        conditions: []
      },
      titleCount: {
        conditions: []
      },
      modal: false
    }).show()
  }
}
