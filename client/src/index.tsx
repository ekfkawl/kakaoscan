import React from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import { store } from "./app/store";
import App from "./App";
import "./index.css";
import DarkModeToggle from "./components/DarkModeToggle";

const container = document.getElementById("root")!;
const root = createRoot(container);

root.render(
  <React.StrictMode>
    <Provider store={store}>
      <DarkModeToggle />
      <App />
    </Provider>
  </React.StrictMode>,
);
