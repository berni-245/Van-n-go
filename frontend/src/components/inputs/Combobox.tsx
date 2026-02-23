import {Component, createSignal, createMemo, Show, For, onMount, onCleanup} from "solid-js";
import {HtmlIdFamily} from "@/components/htmlIds";

export type ComboboxOption = {
    label: string;
    value: string | number;
};

type ComboboxProps = {
    name: string;
    label?: string;
    options: ComboboxOption[];
    value?: string | number;
    placeholder?: string;
    error?: string | string[];
};

const Combobox: Component<ComboboxProps> = (props) => {
    const inputId = new HtmlIdFamily("combobox");
    let containerRef: HTMLDivElement | undefined;


    const [isOpen, setIsOpen] = createSignal(false);
    const [search, setSearch] = createSignal("");


    const initialOption = props.options.find(o => o.value === props.value) || null;
    const [selected, setSelected] = createSignal<ComboboxOption | null>(initialOption);


    const filteredOptions = createMemo(() => {
        if (!search()) return props.options;
        return props.options.filter((opt) =>
            opt.label.toLowerCase().includes(search().toLowerCase())
        );
    });


    const handleClickOutside = (e: MouseEvent) => {
        if (containerRef && !containerRef.contains(e.target as Node)) {
            setIsOpen(false);
        }
    };

    onMount(() => document.addEventListener("click", handleClickOutside));
    onCleanup(() => document.removeEventListener("click", handleClickOutside));

    const handleSelect = (option: ComboboxOption) => {
        setSelected(option);
        setIsOpen(false);
        setSearch(""); // Limpiamos la búsqueda
    };

    return (
        <div ref={containerRef}
             class="relative flex w-full max-w-xs flex-col gap-1 text-on-surface dark:text-on-surface-dark">


            <label
                for={inputId.id()}
                class="flex w-fit items-center gap-1 pl-0.5 text-sm"
                classList={{"text-danger": !!props.error}}
            >
                <Show when={props.error}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" aria-hidden="true" fill="currentColor"
                         class="w-4 h-4">
                        <path
                            d="M5.28 4.22a.75.75 0 0 0-1.06 1.06L6.94 8l-2.72 2.72a.75.75 0 1 0 1.06 1.06L8 9.06l2.72 2.72a.75.75 0 1 0 1.06-1.06L9.06 8l2.72-2.72a.75.75 0 0 0-1.06-1.06L8 6.94 5.28 4.22Z"/>
                    </svg>
                </Show>
                {props.label}
            </label>


            <input type="hidden" name={props.name} value={selected()?.value || ""}/>

            <div class="relative">

                <button
                    type="button"
                    onClick={() => setIsOpen(!isOpen())}
                    class="inline-flex w-full items-center justify-between gap-2 rounded-radius border bg-surface-alt px-4 py-2 text-sm font-medium tracking-wide text-on-surface transition hover:opacity-75 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary dark:bg-surface-dark-alt/50 dark:text-on-surface-dark dark:focus-visible:outline-primary-dark"
                    classList={{
                        "border-danger": !!props.error,
                        "border-outline dark:border-outline-dark": !props.error
                    }}
                    role="combobox"
                    aria-expanded={isOpen()}
                >
          <span class="text-sm font-normal truncate">
            {selected() ? selected()!.label : (props.placeholder || "Seleccionar...")}
          </span>

                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"
                         class="size-5 shrink-0" aria-hidden="true">
                        <path fill-rule="evenodd"
                              d="M5.22 8.22a.75.75 0 0 1 1.06 0L10 11.94l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 9.28a.75.75 0 0 1 0-1.06Z"
                              clip-rule="evenodd"/>
                    </svg>
                </button>


                <Show when={isOpen()}>
                    <div
                        class="absolute left-0 top-11 z-50 w-full overflow-hidden rounded-radius border border-outline bg-surface-alt shadow-lg dark:border-outline-dark dark:bg-surface-dark-alt">

                        <div class="relative">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" stroke="currentColor"
                                 fill="none" stroke-width="1.5"
                                 class="absolute left-4 top-1/2 size-5 -translate-y-1/2 text-on-surface/50 dark:text-on-surface-dark/50"
                                 aria-hidden="true">
                                <path stroke-linecap="round" stroke-linejoin="round"
                                      d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z"/>
                            </svg>
                            <input
                                type="text"
                                class="w-full border-b border-outline bg-surface-alt py-2.5 pl-11 pr-4 text-sm text-on-surface focus:outline-hidden focus-visible:border-primary dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark dark:focus-visible:border-primary-dark"
                                placeholder="Buscar..."
                                value={search()}
                                onInput={(e) => setSearch(e.currentTarget.value)}
                                autofocus
                            />
                        </div>


                        <ul class="flex max-h-44 flex-col overflow-y-auto py-1">
                            <Show
                                when={filteredOptions().length > 0}
                                fallback={<li
                                    class="px-4 py-2 text-sm text-on-surface/60 dark:text-on-surface-dark/60">Sin
                                    resultados</li>}
                            >
                                <For each={filteredOptions()}>
                                    {(item) => (
                                        <li
                                            class="inline-flex cursor-pointer justify-between gap-6 px-4 py-2 text-sm text-on-surface hover:bg-surface-dark-alt/5 hover:text-on-surface-strong dark:text-on-surface-dark dark:hover:bg-surface-alt/5 dark:hover:text-on-surface-dark-strong"
                                            classList={{"font-bold bg-surface-dark-alt/5 dark:bg-surface-alt/10": selected()?.value === item.value}}
                                            onClick={() => handleSelect(item)}
                                        >
                                            <span class="truncate">{item.label}</span>
                                            <Show when={selected()?.value === item.value}>
                                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                                                     stroke="currentColor" fill="none" stroke-width="2"
                                                     class="size-4 shrink-0" aria-hidden="true">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                          d="m4.5 12.75 6 6 9-13.5"/>
                                                </svg>
                                            </Show>
                                        </li>
                                    )}
                                </For>
                            </Show>
                        </ul>
                    </div>
                </Show>
            </div>


            <Show when={props.error}>
                <div class="flex flex-col gap-0.5">
                    <Show when={Array.isArray(props.error)}
                          fallback={<small class="pl-0.5 text-danger">{props.error as string}</small>}>
                        <For each={props.error as string[]}>
                            {(err) => <small class="pl-0.5 text-danger">• {err}</small>}
                        </For>
                    </Show>
                </div>
            </Show>

        </div>
    );
};

export default Combobox;