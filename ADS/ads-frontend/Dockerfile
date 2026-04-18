# Production UI: static build + nginx reverse proxy to ads-backend for /api.
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
# Copy full app so `tsc` + `vite build` see tsconfig, src, and any future root assets.
COPY . .
# Same-origin /api via nginx — leave VITE_API_BASE unset.
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
