const RELS = ["first", "last", "next", "prev"] as const;
export type Rel = (typeof RELS)[number];
export const REL_SET: ReadonlySet<string> = new Set(RELS);

type RequiredRel = Extract<Rel, "first" | "last">;
type OptionalRel = Exclude<Rel, RequiredRel>;

// export type PageInfo = Partial<Record<Rel, PageLinkHeader>>;
export type PageInfo = Record<RequiredRel, PageLinkHeader> & Partial<Record<OptionalRel, PageLinkHeader>>;

export interface PaginatedResource<T> {
    data: T[];
    pages: PageInfo;
}

interface PageLinkHeader {
    url: string;
    pageNumber: number;
    rel: Rel;
}

function parsePageLinkHeader(link: string): PageLinkHeader | null {
    const parts = link.split("; ");
    if (parts.length !== 2) return null;
    const url = parts[0].match(/<(.*)>/)?.[1];
    if (!url) return null;
    const rel = parts[1].match(/"(.*)"/)?.[1];
    if (!rel || !REL_SET.has(rel)) return null;
    const pageStr = parts[0].match(/\?page=(\d+)/)?.[1];
    if (!pageStr) return null;
    const page = parseInt(pageStr);
    if (!Number.isInteger(page)) return null;
    return {url, pageNumber: page, rel: rel as Rel};
}

export function parsePageInfo(res: Response): PageInfo | null {
    const linkHeader = res.headers.get("link");
    if (!linkHeader) return null;

    const result: Partial<Record<Rel, PageLinkHeader>> = {};

    for (const link of linkHeader.split(", ")) {
        const page = parsePageLinkHeader(link);
        if (page) {
            result[page.rel] = page;
        }
    }

    if (!result.first || !result.last) {
        return null;
    }

    return result as PageInfo;
}
