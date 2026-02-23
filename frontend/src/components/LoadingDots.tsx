import {Component, Ref} from "solid-js";
import {useTranslator} from "~/src/app-context/AppContext";

interface Props {
    ref?: Ref<HTMLDivElement>;
}

const LoadingDots: Component<Props> = (props) => {
    const t = useTranslator();

    return (
        <div ref={props.ref} class="flex items-center justify-center gap-2 py-6" role="status" aria-live="polite">
            <div class="flex gap-2 motion-safe:animate-pulse">
                <span
                    class="size-2 rounded-full bg-on-surface/40 dark:bg-on-surface-dark/40 animate-bounce [animation-delay:-0.2s]"/>
                <span
                    class="size-2 rounded-full bg-on-surface/40 dark:bg-on-surface-dark/40 animate-bounce [animation-delay:-0.1s]"/>
                <span class="size-2 rounded-full bg-on-surface/40 dark:bg-on-surface-dark/40 animate-bounce"/>
            </div>
            <span class="sr-only">{t("global.sr.loading")}</span>
        </div>
    );
};

export default LoadingDots;
