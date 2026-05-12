/// <reference types="vite/client" />
declare interface ImportMetaEnv {
  readonly VITE_API_URL: string;
  readonly [key: string]: string | boolean | undefined;
}

declare interface ImportMeta {
  readonly env: ImportMetaEnv;
}
