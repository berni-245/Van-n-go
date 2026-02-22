import { Client } from "@/api/types.ts";
import {useApi, useTranslator} from "@/app-context/AppContext.tsx";
import {createResource, Show} from "solid-js";
import Combobox, {ComboboxOption} from "@components/inputs/Combobox.tsx";


export default function EditClientProfile(props: { user: Client,  errors?: Record<string, string> } ) {
    const t = useTranslator();

    const api = useApi();

    const [zones] = createResource(async () => {
        return await api.getZones();
    });


    return (
        <>
            <div class="space-y-4">
                   <Show
                    when={zones()}
                    fallback={<div class="text-sm text-gray-500 animate-pulse">Cargando zonas...</div>}
                >
                    {(zoneList) => {

                        const options: ComboboxOption[] = zoneList().map(z => ({
                            value: z.id,
                            label: `${z.neighborhood.name}, ${z.province.name}, ${z.country.name}`
                        }));

                       console.log("User's current zone:", props.user.zone);
                        const currentZone = zoneList().find(z => z.self === props.user.zone);

                        return (
                            <Combobox
                                name="zoneId"
                                label={t("global.generic.word.zone")}
                                placeholder="Seleccioná una zona"
                                options={options}
                                value={currentZone ? currentZone.id : undefined}
                                error={props.errors?.["zoneId"]}
                            />
                        );
                    }}
                </Show>
            </div>
        </>
    );
}