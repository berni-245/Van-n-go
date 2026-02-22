import {Component} from "solid-js";
// import { HtmlIdFamily } from "@components/htmlIds";
import {BsChatDots, BsJournalText} from 'solid-icons/bs'
import {useTranslator} from "@/app-context/AppContext";
import {Booking, Client, Zone} from "@/api/types";
import ImageWithFallback from "../ImageWithFallback";

type CardProps = {
    booking: Booking;
    client: Client;
    originZone: Zone;
    destinationZone: Zone;
};


const BookingCardClient: Component<CardProps> = (props) => {
    const t = useTranslator();
    // const cardId = new HtmlIdFamily("card");

    return (
        <article
            class="group max-w-sm overflow-hidden rounded-radius border border-outline bg-surface-alt text-on-surface shadow-sm transition hover:shadow-md dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
            <div class="grid grid-cols-[1fr_auto] gap-4 p-4 items-center">
                <div class="flex flex-col gap-1">
                  <span class="text-m text-on-surface-weak dark:text-on-surface-dark-weak">
                    {t(
                        `global.page.bookings.${props.booking.shiftPeriod.toLowerCase()}DateTime` as any,
                        {date: props.booking.date}
                    )}
                  </span>
                    <span class="text-sm font-semibold">
                    {props.client.username}
                  </span>
                </div>

                <div class="h-16 w-16 overflow-hidden rounded-full border border-outline dark:border-outline-dark">
                    <ImageWithFallback
                        class="h-full w-full object-cover transition group-hover:scale-105"
                        alt={t("global.sr.vehiclePicture")}
                        src={props.client.profilePic}
                        fallbackSrc="/images/blank_user.jpg"
                        skipSrc={!props.client.hasProfilePic}
                    />
                </div>
            </div>


            <div class="px-4 pb-4">
                <div class="rounded-radius bg-surface px-3 py-2 text-center dark:bg-surface-dark">
                  <span class="text-base font-semibold">
                    {t("global.page.bookings.travelZones", {
                        originNeighborhood: props.originZone.neighborhood.name,
                        destinationNeighborhood: props.destinationZone.neighborhood.name
                    })}
                  </span>
                </div>
            </div>


            <div class="grid grid-cols-2 gap-3 border-t border-outline p-4 dark:border-outline-dark">
                <button type="button"
                        class="inline-flex items-center justify-center gap-2 rounded-radius bg-primary px-4 py-3 text-sm font-medium text-on-primary transition hover:opacity-80 dark:bg-primary-dark dark:text-on-primary-dark">
                    <BsJournalText class="text-lg"/>
                    <span class="hidden sm:inline">{t("global.page.bookings.details")}</span>
                </button>


                <button type="button"
                        class="inline-flex items-center justify-center gap-2 rounded-radius bg-primary px-4 py-3 text-sm font-medium text-on-primary transition hover:opacity-80 dark:bg-primary-dark dark:text-on-primary-dark">
                    <BsChatDots class="text-lg"/>
                    <span class="hidden sm:inline">{t("global.page.bookings.chat")}</span>
                </button>
            </div>
        </article>
    );
};

export default BookingCardClient;
