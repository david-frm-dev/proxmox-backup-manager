# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /frontend

RUN apk add --no-cache python3 make g++

COPY package*.json ./

ENV CYPRESS_INSTALL_BINARY=0

RUN npm i

COPY . .

ENV NX_DAEMON=false

RUN npx nx run frontend:build:production --verbose

# Stage 2: Serve
FROM nginx:stable-alpine
COPY --from=builder /frontend/dist/apps/frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
