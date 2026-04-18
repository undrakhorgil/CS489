/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** Base URL of the ADS API (no trailing slash). Empty in dev when using Vite proxy. */
  readonly VITE_API_BASE?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
