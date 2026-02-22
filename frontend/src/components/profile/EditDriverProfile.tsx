import {  useTranslator } from "@/app-context/AppContext";
import {  Driver } from "@/api/types";
import TextInput from "@components/inputs/TextInput.tsx";



export default function EditDriverProfile(props: { user: Driver, errors?: Record<string, string> }) {
  const t = useTranslator();


  return (
    <>
      <TextInput
        name="description"
        label={t("global.generic.word.description")}
        value={props.user.description}
        error={props.errors?.["description"]}
      />
    </>
  );
}