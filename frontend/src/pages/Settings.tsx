import { useAppState } from "../app-context/AppContext";
import { Locale } from "../app-context/locale";

export default function Settings() {
  const [_, setAppState] = useAppState();
  return (
    <select onchange={(event) => setAppState("preferences", { locale: event.target.value as Locale })}>
      <option value="en">English</option>
      <option value="es">Spanish</option>
    </select>
  );
}
