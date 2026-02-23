import {For, Show} from "solid-js";
import {useTranslator} from "@/app-context/AppContext";
import {FaRegularStar, FaSolidStar} from "solid-icons/fa";
import {Driver} from "@/api/types";


export default function DriverProfileInfo(props: { user: Driver }) {
    const t = useTranslator();


    const ratingInt = () => Math.round(props.user.rating || 0);

    return (
        <>
            <p>
                <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                    {t("global.generic.word.description")}:
                </strong>{" "}
                {props.user.description || t("global.public.profile.noDescription")}
            </p>

            {/* Sistema de Estrellas */}
            <div class="mt-2 flex items-center justify-center gap-3">
                <Show
                    when={props.user.rating != null}
                    fallback={<span class="text-sm italic">{t("global.client.availability.no_rating")}</span>}
                >
          <span class="text-xl font-bold text-warning">
            {props.user.rating}
          </span>
                    <div class="flex items-center gap-1">
                        <For each={Array.from({length: 5})}>
                            {(_, index) => (
                                <Show
                                    when={index() < ratingInt()}
                                    fallback={<FaRegularStar class="text-outline dark:text-outline-dark" size={24}/>}
                                >
                                    <FaSolidStar class="text-warning" size={24}/>
                                </Show>
                            )}
                        </For>
                    </div>
                </Show>
            </div>
        </>
    );
}