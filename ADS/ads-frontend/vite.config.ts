import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Dev: browser calls /api/... on :5173; Vite proxies to Spring Boot on :8080 (no CORS needed).
// Prod build: set VITE_API_BASE to the public API URL (e.g. https://api.example.com) and ensure backend CORS allows your site origin.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
