import { createSignal, For, onCleanup, Show } from "solid-js";
import { BsX } from "solid-icons/bs";
import { useApi } from "@/app-context/AppContext";

export type ZoneDTO = {
  id: number;
  country?: { name?: string; code?: string };
  province?: { name?: string };
  neighborhood?: { name?: string };
};

type ZoneInputProps = {
  label: string;
  value: string;
  onInput: (value: string) => void;
  onSelect: (zoneId: number | null) => void;
  error?: string | null;
  excludeIds?: number[];
  includeIds?: number[];
  class?: string;
};

// Builds the "neighborhood, province, country" display label, dropping any missing parts.
export function zoneDisplay(z: ZoneDTO): string {
  const n = z.neighborhood?.name ?? "";
  const p = z.province?.name ?? "";
  const c = z.country?.name ?? "";
  return [n, p, c].filter(Boolean).join(", ");
}

export default function ZoneInput(props: ZoneInputProps) {
  const api = useApi();
  const [zones, setZones] = createSignal<ZoneDTO[] | null>(null);
  const [showSuggestions, setShowSuggestions] = createSignal(false);
  const [highlightIndex, setHighlightIndex] = createSignal(-1);

  // debounce timer
  let fetchTimer: number | undefined;

  async function fetchZones() {
    if (zones()) return zones();
    try {
      const json = await api.getZones();
      setZones(json as ZoneDTO[]);
      return json as ZoneDTO[];
    } catch (err) {
      console.error("Zone fetch failed:", err);
      setZones([]);
      return [];
    }
  }

  function filteredZones(q: string) {
    if (!q) return [];
    const ql = q.toLowerCase();
    return (zones() ?? [])
      .filter((z) => !props.excludeIds?.includes(z.id))
      .filter((z) => !props.includeIds || props.includeIds.includes(z.id))
      .map((z) => ({ z, label: zoneDisplay(z) }))
      .filter(({ label }) => label.toLowerCase().includes(ql))
      .slice(0, 10); // limit suggestions
  }

  onCleanup(() => {
    if (fetchTimer) clearTimeout(fetchTimer);
  });

  let inputRef: HTMLInputElement | undefined;

  function clear() {
    props.onInput("");
    props.onSelect(null as any);
    setShowSuggestions(false);
    inputRef?.focus();
  }

  return (
    <div class={`flex w-full flex-col gap-1 text-on-surface dark:text-on-surface-dark ${props.class ?? "max-w-xs"}`}>
      <label class="flex w-fit items-center gap-1 pl-0.5 text-sm" classList={{ "text-danger": !!props.error }}>
        {props.label}
      </label>
      <div class="relative w-full">
        <input
          ref={inputRef}
          type="text"
          class="w-full rounded-radius border bg-surface-alt px-2 py-2 pr-8 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:bg-surface-dark-alt/50 dark:focus-visible:outline-primary-dark"
          classList={{ "border-danger": !!props.error }}
          value={props.value}
          onInput={(e) => {
            const v = e.currentTarget.value;
            props.onInput(v);
            props.onSelect(null as any);
            setShowSuggestions(true);

            if (fetchTimer) window.clearTimeout(fetchTimer);

            fetchTimer = window.setTimeout(async () => {
              await fetchZones();
              setHighlightIndex(-1);
            }, 180);
          }}
          onFocus={async () => {
            await fetchZones();
            setShowSuggestions(true);
          }}
          onKeyDown={(e) => {
            const list = filteredZones(props.value);
            if (e.key === "ArrowDown") {
              e.preventDefault();
              setHighlightIndex(Math.min(highlightIndex() + 1, list.length - 1));
            } else if (e.key === "ArrowUp") {
              e.preventDefault();
              setHighlightIndex(Math.max(highlightIndex() - 1, 0));
            } else if (e.key === "Enter") {
              if (showSuggestions() && highlightIndex() >= 0) {
                e.preventDefault();
                const sel = list[highlightIndex()];
                if (sel) {
                  props.onInput(sel.label);
                  props.onSelect(sel.z.id);
                  setShowSuggestions(false);
                }
              }
            } else if (e.key === "Escape") {
              setShowSuggestions(false);
            }
          }}
          onBlur={() => {
            setTimeout(() => {
              const q = props.value.trim().toLowerCase();
              const matches = (zones() ?? [])
                .filter((z) => !props.excludeIds?.includes(z.id))
                .filter((z) => !props.includeIds || props.includeIds.includes(z.id))
                .map((z) => ({ z, label: zoneDisplay(z) }))
                .filter(({ label }) => label.toLowerCase() === q);

              if (matches.length === 1) {
                props.onInput(matches[0].label);
                props.onSelect(matches[0].z.id);
              } else {
                props.onSelect(null as any);
              }

              setShowSuggestions(false);
            }, 120);
          }}
        />

        <Show when={props.value}>
          <button
            type="button"
            aria-label="Clear zone"
            class="absolute inset-y-0 right-0 flex items-center px-2 text-on-surface/50 hover:text-on-surface dark:text-on-surface-dark/50 dark:hover:text-on-surface-dark"
            onMouseDown={(ev) => ev.preventDefault()}
            onClick={clear}
          >
            <BsX size={18} />
          </button>
        </Show>

        {showSuggestions() && filteredZones(props.value).length > 0 && (
          <ul class="absolute top-full z-50 mt-1 max-h-48 w-full overflow-auto rounded-radius border border-outline bg-surface-alt shadow-lg dark:border-outline-dark dark:bg-surface-dark-alt">
            <For each={filteredZones(props.value)}>
              {({ z, label }, i) => {
                const idx = i();
                const highlighted = idx === highlightIndex();
                return (
                  <li
                    class="cursor-pointer px-3 py-2 text-sm"
                    classList={{
                      "bg-primary/10 dark:bg-primary-dark/20": highlighted,
                      "hover:bg-surface dark:hover:bg-surface-dark": !highlighted,
                    }}
                    onMouseEnter={() => setHighlightIndex(idx)}
                    onMouseDown={(ev) => {
                      ev.preventDefault();
                      props.onInput(label);
                      props.onSelect(z.id);
                      setShowSuggestions(false);
                    }}
                  >
                    <div class="font-medium">{label}</div>
                    <div class="text-xs text-outline dark:text-outline-dark">{z.country?.code}</div>
                  </li>
                );
              }}
            </For>
          </ul>
        )}
      </div>

      {props.error && <small class="pl-0.5 text-danger">{props.error}</small>}
    </div>
  );
}
