import { createSignal } from "solid-js";
import ZoneInput from "@/components/inputs/ZoneInput";
import Button from "@/components/Button";
import { useTranslator } from "@/app-context/AppContext";

export default function Search() {
    const t = useTranslator();

    // zone: visible query and selected id
    const [zoneQuery, setZoneQuery] = createSignal<string>("");
    const [zoneId, setZoneId] = createSignal<number | null>(null);

    // other form fields
    const [size, setSize] = createSignal<string>("");
    const [weekday, setWeekday] = createSignal<string>("");

    const handleSubmit = (e: SubmitEvent) => {
        e.preventDefault();

        const params = new URLSearchParams();
        if (zoneId() !== null) params.append("zoneId", String(zoneId()));
        if (size()) params.append("size", size());
        if (weekday()) params.append("weekday", weekday());

        // navigate with GET as the JSP did
        const qs = params.toString();
        const url = `/client/availability${qs ? "?" + qs : ""}`;
        window.location.href = url;
    };

    return (
        <div class="container d-flex justify-content-center mt-5">
            <div class="col-md-6 col-lg-5 text-center">
                <h1 class="display-5 mb-5">{t("siteName")}</h1>

                <form onSubmit={handleSubmit}>
                    <div class="mb-4">
                        {/* label comes from translator like the JSP */}
                        <ZoneInput
                            label={t("client.search.selectZone" as any)}
                            value={zoneQuery()}
                            onInput={(val) => setZoneQuery(val)}
                            onSelect={(id: number | null) => setZoneId(id)}
                            // show invalid message when user typed something but didn't pick a zone
                            error={zoneQuery().trim() !== "" && zoneId() === null ? (t("client.search.zoneInvalid" as any) as string) : null}
                        />
                    </div>

                    <div class="mb-4">
                        <label for="select-size" class="form-label h4">
                            {t("generic.word.size")}
                        </label>
                        <select
                            id="select-size"
                            class="form-select form-select-lg"
                            value={size()}
                            onInput={(e) => setSize(e.currentTarget.value)}
                        >
                            <option value="">{t("generic.word.any")}</option>
                            <option value="SMALL">{t("generic.word.small")}</option>
                            <option value="MEDIUM">{t("generic.word.medium")}</option>
                            <option value="LARGE">{t("generic.word.large")}</option>
                        </select>
                    </div>

                    <div class="mb-4">
                        <label for="select-day" class="form-label h4">
                            {t("generic.word.weekday")}
                        </label>
                        <select
                            id="select-day"
                            class="form-select form-select-lg"
                            value={weekday()}
                            onInput={(e) => setWeekday(e.currentTarget.value)}
                        >
                            <option value="">{t("generic.word.any")}</option>
                            <option value="MONDAY">{t("day.monday")}</option>
                            <option value="TUESDAY">{t("day.tuesday")}</option>
                            <option value="WEDNESDAY">{t("day.wednesday")}</option>
                            <option value="THURSDAY">{t("day.thursday")}</option>
                            <option value="FRIDAY">{t("day.friday")}</option>
                            <option value="SATURDAY">{t("day.saturday")}</option>
                            <option value="SUNDAY">{t("day.sunday")}</option>
                        </select>
                    </div>

                    <div class="d-grid">
                        <Button type="submit" category="primary" text={t("components.availability.Search") as string} />
                    </div>
                </form>
            </div>
        </div>
    );
}
