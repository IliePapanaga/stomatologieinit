import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { TanStackStartVite } from '@tanstack/start-vite-plugin';

export default defineConfig({
  plugins: [
    react(),
    TanStackStartVite({
      server: {
        preset: 'vercel'
      }
    })
  ],
});