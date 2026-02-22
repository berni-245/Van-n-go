import {Component} from "solid-js";
// import { HtmlIdFamily } from "@components/htmlIds";
import {useTranslator} from "@/app-context/AppContext";
import {Booking, Client} from "@/api/types";
import ImageWithFallback from "../ImageWithFallback";

type CardProps = {
    booking: Booking;
    client: Client
};

const BookingCardClient: Component<CardProps> = (props) => {
    const t = useTranslator();
    // const cardId = new HtmlIdFamily("card");

    return (
        <article
            class="group flex rounded-radius max-w-sm flex-col overflow-hidden border border-outline bg-surface-alt text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
            <div class="h-44 md:h-64 overflow-hidden">
                <ImageWithFallback
                    class="object-contain transition duration-700 ease-out group-hover:scale-105 w-full h-full"
                    alt={t("global.sr.vehiclePicture")}
                    src={props.client.profilePic}
                    fallbackSrc="/images/blank_user.jpg"
                    skipSrc={!props.client.hasProfilePic}
                />
            </div>
            <div class="flex flex-col gap-4 p-6">
                <span class="text-sm font-medium">{props.client.username}</span>

                {/*<div class="grid grid-cols-2 gap-4">*/}
                {/*  <div class="flex flex-col">*/}
                {/*  <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">*/}
                {/*    {t("global.page.vehicles.volume")}*/}
                {/*  </span>*/}
                {/*    <span class="text-lg font-semibold">{props.vehicle.volumeM3} m³</span>*/}
                {/*  </div>*/}

                {/*  <div class="flex flex-col">*/}
                {/*  <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">*/}
                {/*    {t("global.page.vehicles.rate")}*/}
                {/*  </span>*/}
                {/*    <span class="text-lg font-semibold">${props.vehicle.hourlyRate}/h</span>*/}
                {/*  </div>*/}
                {/*</div>*/}

                {/* <div class="flex flex-wrap gap-2"> */}
                {/*   <Badge category="primary"> {props.vehicle.volumeM3} m³</Badge> */}
                {/*   <Badge category="primary"> ${props.vehicle.hourlyRate}/h </Badge> */}
                {/* </div> */}

                {/*<p id={cardId.id("description")} class="text-pretty">*/}
                {/*  {props.vehicle.description}*/}
                {/*</p>*/}
            </div>
        </article>
    );
};

export default BookingCardClient;
