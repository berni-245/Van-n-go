import { createSignal, For, onCleanup } from "solid-js";
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
      .map((z) => ({ z, label: zoneDisplay(z) }))
      .filter(({ label }) => label.toLowerCase().includes(ql))
      .slice(0, 10); // limit suggestions
  }

  onCleanup(() => {
    if (fetchTimer) clearTimeout(fetchTimer);
  });

  return (
    <div class="relative mb-4">
      <label class="block mb-2">{props.label}</label>
      <input
        type="text"
        class="block w-full rounded-md border px-3 py-2 focus:outline-none focus:ring"
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

      {showSuggestions() && filteredZones(props.value).length > 0 && (
        <ul class="absolute z-50 w-full mt-1 max-h-48 overflow-auto rounded-md shadow-lg bg-white dark:bg-gray-800 ring-1 ring-black/5">
          <For each={filteredZones(props.value)}>
            {({ z, label }, i) => {
              const idx = i();
              const highlighted = idx === highlightIndex();
              return (
                <li
                  class={`cursor-pointer px-3 py-2 text-sm ${
                    highlighted ? "bg-blue-100 dark:bg-blue-900" : "hover:bg-gray-50 dark:hover:bg-gray-700"
                  }`}
                  onMouseEnter={() => setHighlightIndex(idx)}
                  onMouseDown={(ev) => {
                    ev.preventDefault();
                    props.onInput(label);
                    props.onSelect(z.id);
                    setShowSuggestions(false);
                  }}
                >
                  <div class="font-medium">{label}</div>
                  <div class="text-xs text-gray-500">{z.country?.code}</div>
                </li>
              );
            }}
          </For>
        </ul>
      )}

      {props.error && <p class="text-red-600 mt-1 text-sm">{props.error}</p>}
    </div>
  );
}
