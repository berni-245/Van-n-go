import { createSignal, For, Show, onMount } from "solid-js";
import { useSearchParams, useNavigate } from "@solidjs/router";
import { Title } from "@solidjs/meta";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import ImageWithFallback from "@/components/ImageWithFallback";
import { Message } from "@/api/types";

export default function Chat() {
    const t = useTranslator();
    const api = useApi();
    const [appState] = useAppState();
    const [searchParams] = useSearchParams();

    const navigate = useNavigate();
    const driverId = () => Number(searchParams.driverId);
    const clientId = () => Number(searchParams.clientId);
    const isDriver = () => appState.user?.type === "driver";

    const [messages, setMessages] = createSignal<Message[]>([]);
    const [page, setPage] = createSignal(1);
    const [maxPages, setMaxPages] = createSignal(1);
    const [loadingMore, setLoadingMore] = createSignal(false);
    const [sending, setSending] = createSignal(false);
    const [input, setInput] = createSignal("");

    const [otherName, setOtherName] = createSignal("");
    const [otherPfp, setOtherPfp] = createSignal<string | undefined>(undefined);
    const [otherHasPfp, setOtherHasPfp] = createSignal(false);

    let messagesEndRef: HTMLDivElement | undefined;

    const scrollToBottom = (smooth = true) => {
        messagesEndRef?.scrollIntoView({ behavior: smooth ? "smooth" : "instant" });
    };

    const loadMessages = async (p: number, prepend = false) => {
        setLoadingMore(true);
        try {
            const result = await api.getMessages(driverId(), clientId(), p);
            if (!result) {
                const role = isDriver() ? "driver" : "client";
                navigate(`/${role}/bookings`, { replace: true });
                return;
            }
            const { messages: msgs, maxPages: mp } = result;
            setMaxPages(mp);
            // API returns newest-first; reverse so oldest appears at top
            const ordered = [...msgs].reverse();
            if (prepend) {
                setMessages((prev) => [...ordered, ...prev]);
            } else {
                setMessages(ordered);
            }
        } catch (err: any) {
            const role = isDriver() ? "driver" : "client";
            navigate(`/${role}/bookings`, { replace: true });
        } finally {
            setLoadingMore(false);
        }
    };

    const loadOtherParty = async () => {
        if (isDriver()) {
            const client = await api.getClient(clientId(), { visibility: "public" });
            if (client) {
                setOtherName(client.username);
                setOtherPfp(client.profilePic);
                setOtherHasPfp(client.hasProfilePic ?? false);
            }
        } else {
            const driver = await api.getDriver(driverId(), { visibility: "public" });
            if (driver) {
                setOtherName(driver.username);
                setOtherPfp(driver.profilePic);
                setOtherHasPfp(driver.hasProfilePic ?? false);
            }
        }
    };

    onMount(async () => {
        await Promise.all([loadMessages(1), loadOtherParty()]);
        scrollToBottom(false);
    });

    const handleLoadOlder = () => {
        const nextPage = page() + 1;
        setPage(nextPage);
        loadMessages(nextPage, true);
    };

    const handleSend = async () => {
        const text = input().trim();
        if (!text || sending()) return;
        setSending(true);
        try {
            const msg = await api.sendMessage(driverId(), clientId(), text);
            if (msg) {
                setMessages((prev) => [...prev, msg]);
                setInput("");
                setTimeout(() => scrollToBottom(), 50);
            }
        } finally {
            setSending(false);
        }
    };

    const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    const formatTime = (iso: string) =>
        new Date(iso).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });

    /** Returns "YYYY-MM-DD" for grouping messages by day */
    const getDateKey = (iso: string) => iso.slice(0, 10);

    /** Returns "Today", "Yesterday", or a locale date string */
    const formatDateLabel = (iso: string) => {
        const msgDate = new Date(iso);
        const today = new Date();
        const yesterday = new Date();
        yesterday.setDate(today.getDate() - 1);

        const sameDay = (a: Date, b: Date) =>
            a.getFullYear() === b.getFullYear() &&
            a.getMonth() === b.getMonth() &&
            a.getDate() === b.getDate();

        if (sameDay(msgDate, today)) return t("chat.today" as any) as string || "Today";
        if (sameDay(msgDate, yesterday)) return t("chat.yesterday" as any) as string || "Yesterday";
        return msgDate.toLocaleDateString([], { year: "numeric", month: "long", day: "numeric" });
    };

    const myInitials = () =>
        (appState.user?.username ?? "").slice(0, 2).toUpperCase();

    return (
        <>
            <Title>Chat - Van N' Go</Title>
            <div class="flex h-[calc(100vh-4rem)] flex-col">

                {/* ── Header ── */}
                <div class="flex items-center gap-3 border-b border-outline bg-surface-alt px-4 py-3 dark:border-outline-dark dark:bg-surface-dark-alt">
                    <div class="h-10 w-10 shrink-0 overflow-hidden rounded-full border border-outline dark:border-outline-dark">
                        <ImageWithFallback
                            src={otherPfp()}
                            fallbackSrc="/images/default-user.png"
                            skipSrc={!otherHasPfp()}
                            class="h-full w-full object-cover"
                            alt={otherName()}
                        />
                    </div>
                    <span class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                        {otherName()}
                    </span>
                </div>

                {/* ── Message list ── */}
                <div class="flex flex-1 flex-col overflow-y-auto px-4 py-6">

                    {/* Load older */}
                    <Show when={page() < maxPages()}>
                        <div class="mb-4 text-center">
                            <button
                                class="text-sm text-on-surface underline opacity-70 hover:opacity-100 disabled:opacity-40 dark:text-on-surface-dark"
                                onClick={handleLoadOlder}
                                disabled={loadingMore()}
                            >
                                {loadingMore()
                                    ? (t("global.sr.loading" as any) as string)
                                    : (t("chat.loadOlder" as any) as string)}
                            </button>
                        </div>
                    </Show>

                    <div class="flex flex-col gap-4">
                        <For each={messages()}>
                            {(msg, index) => {
                                const isMine = isDriver() ? msg.sentByDriver : !msg.sentByDriver;
                                const prevMsg = () => messages()[index() - 1];
                                const showDateSeparator = () =>
                                    !prevMsg() || getDateKey(prevMsg().timeSent) !== getDateKey(msg.timeSent);

                                return (
                                    <>
                                        {/* ── Date separator ── */}
                                        <Show when={showDateSeparator()}>
                                            <div class="flex items-center gap-3 py-2">
                                                <div class="h-px flex-1 bg-outline opacity-40 dark:bg-outline-dark" />
                                                <span class="rounded-full bg-surface-alt px-3 py-1 text-xs font-medium text-on-surface opacity-60 dark:bg-surface-dark-alt dark:text-on-surface-dark">
                                                    {formatDateLabel(msg.timeSent)}
                                                </span>
                                                <div class="h-px flex-1 bg-outline opacity-40 dark:bg-outline-dark" />
                                            </div>
                                        </Show>

                                        <Show
                                            when={isMine}
                                            fallback={
                                                /* ── Received bubble ── */
                                                <div class="flex items-end gap-2">
                                                    <div class="h-8 w-8 shrink-0 overflow-hidden rounded-full border border-outline dark:border-outline-dark">
                                                        <ImageWithFallback
                                                            src={otherPfp()}
                                                            fallbackSrc="/images/default-user.png"
                                                            skipSrc={!otherHasPfp()}
                                                            class="h-full w-full object-cover"
                                                            alt={otherName()}
                                                        />
                                                    </div>
                                                    <div class="mr-auto flex max-w-[70%] flex-col gap-2 rounded-r-radius rounded-tl-radius bg-surface-alt p-4 text-on-surface md:max-w-[60%] dark:bg-surface-dark-alt dark:text-on-surface-dark">
                                                        <span class="text-sm font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                                                            {otherName()}
                                                        </span>
                                                        <div class="text-sm whitespace-pre-wrap break-words">{msg.content}</div>
                                                        <span class="ml-auto text-xs opacity-60">{formatTime(msg.timeSent)}</span>
                                                    </div>
                                                </div>
                                            }
                                        >
                                            {/* ── Sent bubble ── */}
                                            <div class="flex items-end gap-2">
                                                <div class="ml-auto flex max-w-[70%] flex-col gap-2 rounded-l-radius rounded-tr-radius bg-primary p-4 text-sm text-on-primary md:max-w-[60%] dark:bg-primary-dark dark:text-on-primary-dark">
                                                    <div class="whitespace-pre-wrap break-words">{msg.content}</div>
                                                    <span class="ml-auto text-xs opacity-70">{formatTime(msg.timeSent)}</span>
                                                </div>
                                                <span class="flex h-8 w-8 shrink-0 items-center justify-center overflow-hidden rounded-full border border-outline bg-surface-alt text-xs font-bold tracking-wider text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
                                                    {myInitials()}
                                                </span>
                                            </div>
                                        </Show>
                                    </>
                                );
                            }}
                        </For>
                    </div>

                    <div ref={messagesEndRef} />
                </div>

                {/* ── Input area ── */}
                <div class="border-t border-outline bg-surface-alt px-4 py-3 dark:border-outline-dark dark:bg-surface-dark-alt">
                    <div class="flex items-end gap-2">
                        <textarea
                            rows={1}
                            class="flex-1 resize-none rounded-radius border border-outline bg-surface px-3 py-2 text-sm text-on-surface focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark dark:text-on-surface-dark"
                            placeholder={t("chat.inputPlaceholder" as any) as string}
                            value={input()}
                            onInput={(e) => setInput(e.currentTarget.value)}
                            onKeyDown={handleKeyDown}
                        />
                        <button
                            class="flex h-9 items-center justify-center rounded-radius bg-primary px-4 text-sm font-medium text-on-primary disabled:opacity-50 dark:bg-primary-dark dark:text-on-primary-dark"
                            onClick={handleSend}
                            disabled={sending() || !input().trim()}
                        >
                            {t("global.generic.word.send" as any)}
                        </button>
                    </div>
                    <p class="mt-1 text-xs opacity-50 text-on-surface dark:text-on-surface-dark">
                        {t("chat.enterHint" as any) as string}
                    </p>
                </div>

            </div>
        </>
    );
}