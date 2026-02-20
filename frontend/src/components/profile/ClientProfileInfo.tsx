
import {  Show} from "solid-js";
import {  useTranslator } from "@/app-context/AppContext";
import { Client } from "@/api/types";

export default function ClientProfileInfo(props: { user: Client }) {
  const t = useTranslator();
  return (
    <Show when={props.user.zone}>
      <p>
        <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
          {t("global.generic.word.zone")}:
        </strong>{" "}
        {props.user.zone}
      </p>
    </Show>
  );
}
