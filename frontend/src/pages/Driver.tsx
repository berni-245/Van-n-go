import { Suspense } from "solid-js";

import { useAppState } from "../app-context/AppContext";

export default function Driver() {

  const [appState, _] = useAppState();

  return (
    <div>
      <Suspense fallback="Loading...">{appState.user ? JSON.stringify(appState.user) : ""}</Suspense>
    </div>
  );
}
