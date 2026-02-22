import { Client } from "@/api/types.ts";
import { useTranslator } from "@/app-context/AppContext.tsx";
import TextInput from "@components/inputs/TextInput.tsx";


export default function EditClientProfile(props: { user: Client,  errors?: Record<string, string> } ) {
    const t = useTranslator();


    return (
        <>
            <TextInput
                name="zoneId"
                label={t("global.generic.word.zone")}
                value={props.user.zone}
                error={props.errors?.["zoneId"]}
            />
        </>
    );
}