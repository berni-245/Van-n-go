import { createUniqueId } from "solid-js";

export class HtmlIdFamily {
  elementId: string;

  constructor(prefix?: string) {
    this.elementId = createUniqueId();
    if (prefix) this.elementId = `${prefix}-${this.elementId}`;
  }

  id(postfix?: string) {
    return postfix ? `${this.elementId}-${postfix}` : this.elementId;
  }
}
