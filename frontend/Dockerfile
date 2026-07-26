FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY public ./public
COPY src ./src
RUN npm test -- --watchAll=false && npm run build

FROM nginx:1.27-alpine
ENV PORT=80
ENV API_BASE_URL=http://localhost:8080

COPY --from=build /app/build /usr/share/nginx/html
COPY docker /templates

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -qO- "http://127.0.0.1:${PORT}/health" || exit 1

CMD ["/bin/sh", "-c", "envsubst '${PORT}' < /templates/default.conf.template > /etc/nginx/conf.d/default.conf && envsubst '${API_BASE_URL}' < /templates/config.js.template > /usr/share/nginx/html/config.js && exec nginx -g 'daemon off;'"]
