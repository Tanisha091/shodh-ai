FROM node:20-alpine
WORKDIR /app
COPY package.json package-lock.json* pnpm-lock.yaml* yarn.lock* ./
RUN npm install --legacy-peer-deps \
  && chmod -R +x node_modules/.bin || true
COPY . .
EXPOSE 3000
CMD ["node","node_modules/next/dist/bin/next","dev","-p","3000","-H","0.0.0.0"]
