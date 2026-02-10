import * as i18n from "@solid-primitives/i18n";
import { dict as en_dict } from "~/lang/en/en";
import * as z from "zod";

type RawDictionary = typeof en_dict;

type Locale = z.infer<typeof LocaleSchema>;
export const LocaleSchema = z.enum(["en", "es"]);

type DeepPartial<T> = T extends Record<string, unknown> ? { [K in keyof T]?: DeepPartial<T[K]> } : T;

const raw_dict_map: Record<Locale, () => Promise<{ dict: DeepPartial<RawDictionary> }>> = {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-explicit-any
  en: () => null as any, // en is loaded by default
  es: () => import("~/lang/es/es"),
};

export type Dictionary = i18n.Flatten<RawDictionary>;

export const en_flat_dict: Dictionary = i18n.flatten(en_dict);

export async function fetchDictionary(locale: Locale): Promise<Dictionary> {
  if (locale === "en") return en_flat_dict;

  const { dict } = await raw_dict_map[locale]();
  const flat_dict = i18n.flatten(dict) as RawDictionary;
  return { ...en_flat_dict, ...flat_dict };
}

export const toLocale = (string: string): Locale | undefined =>
  string in raw_dict_map ? (string as Locale) : undefined;

/**
 * Initialize with browser locale or defaulting to englinsh.
 */
export function initialLocale(): Locale {
  let locale: Locale | undefined;

  locale = toLocale(navigator.language.slice(0, 2));
  if (locale) return locale;

  locale = toLocale(navigator.language.toLocaleLowerCase());
  if (locale) return locale;

  return "en";
}
