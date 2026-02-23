import {Component, Ref} from "solid-js";
import { HtmlIdFamily } from "@components/htmlIds";
import { useTranslator } from "@/app-context/AppContext";
import { Vehicle } from "@/api/types";
import ImageWithFallback from "../ImageWithFallback";
import { A } from "@solidjs/router";

type CardProps = {
  vehicle: Vehicle;
  ref?: Ref<HTMLAnchorElement>;
};

const VehicleCard: Component<CardProps> = (props) => {
  const t = useTranslator();
  const cardId = new HtmlIdFamily("card");

  return (
      <A ref={props.ref} href={props.vehicle.id.toString()}>
      <article class="group flex rounded-radius max-w-sm flex-col overflow-hidden border border-outline bg-surface-alt text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
        <div class="h-44 md:h-64 overflow-hidden">
          <ImageWithFallback
            class="object-contain transition duration-700 ease-out group-hover:scale-105 w-full h-full"
            alt={t("global.sr.vehiclePicture")}
            src={props.vehicle.picture}
            fallbackSrc="/images/default-vehicle.png"
            skipSrc={!props.vehicle.hasPicture}
          />
        </div>
        <div class="flex flex-col gap-4 p-6">
          <span class="text-sm font-medium">{props.vehicle.plateNumber}</span>

          <div class="grid grid-cols-2 gap-4">
            <div class="flex flex-col">
              <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
                {t("global.page.vehicles.volume")}
              </span>
              <span class="text-lg font-semibold">{props.vehicle.volumeM3} m³</span>
            </div>

            <div class="flex flex-col">
              <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
                {t("global.page.vehicles.rate")}
              </span>
              <span class="text-lg font-semibold">${props.vehicle.hourlyRate}/h</span>
            </div>
          </div>

          {/* <div class="flex flex-wrap gap-2"> */}
          {/*   <Badge category="primary"> {props.vehicle.volumeM3} m³</Badge> */}
          {/*   <Badge category="primary"> ${props.vehicle.hourlyRate}/h </Badge> */}
          {/* </div> */}

          <p id={cardId.id("description")} class="text-pretty">
            {props.vehicle.description}
          </p>
        </div>
      </article>
    </A>
  );
};

export default VehicleCard;
