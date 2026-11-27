import { Client } from "@/api/types.ts";
import { useApi, useTranslator } from "@/app-context/AppContext.tsx";
import { createEffect, createResource, createSignal } from "solid-js";
import ZoneInput, { zoneDisplay } from "@components/inputs/ZoneInput.tsx";

export default function EditClientProfile(props: { user: Client; errors?: Record<string, string[]> }) {
  const t = useTranslator();

  const api = useApi();

  const [zones] = createResource(async () => {
    return await api.getZones();
  });

  const [zoneQuery, setZoneQuery] = createSignal("");
  const [zoneId, setZoneId] = createSignal<number | null>(null);

  let isInitialized = false;
  createEffect(() => {
    const zoneList = zones();
    if (zoneList && !isInitialized) {
      const currentZone = zoneList.find((z) => z.self === props.user.zone);
      if (currentZone) {
        setZoneQuery(zoneDisplay(currentZone));
        setZoneId(currentZone.id);
      }
      isInitialized = true;
    }
  });

  return (
    <div class="space-y-4">
      <ZoneInput
        label={t("global.generic.word.zone")}
        value={zoneQuery()}
        onInput={setZoneQuery}
        onSelect={setZoneId}
        error={props.errors?.["zoneId"]?.[0] ?? null}
      />
      <input type="hidden" name="zoneId" value={zoneId() ?? ""} />
    </div>
  );
}
