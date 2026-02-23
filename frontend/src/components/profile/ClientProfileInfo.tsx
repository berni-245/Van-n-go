import {createResource, Show} from "solid-js";
import {useApi, useTranslator} from "@/app-context/AppContext";
import {Client} from "@/api/types";

export default function ClientProfileInfo(props: { user: Client }) {
    const t = useTranslator();
    const api = useApi();


    const [zone] = createResource(
        () => props.user.zone,
        async (zoneUri) => await api.getZoneByUri(zoneUri)
    );
    return (
        <Show when={props.user.zone}>
            <p>
                <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                    {t("global.generic.word.zone")}:
                </strong>{" "}
                <Show
                    when={zone()}
                    fallback={<span class="animate-pulse text-on-surface/50 dark:text-on-surface-dark/50">...</span>}
                >
                    {(z) => `${z().neighborhood.name}, ${z().province.name}`}
                </Show>
            </p>
        </Show>
    );
}
