import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'web-address',
                loadChildren: './web-address/web-address.module#CensorshipDetectorWebAddressModule'
            },
            {
                path: 'isp',
                loadChildren: './isp/isp.module#CensorshipDetectorIspModule'
            },
            {
                path: 'censorship-status',
                loadChildren: './censorship-status/censorship-status.module#CensorshipDetectorCensorshipStatusModule'
            },
            {
                path: 'packet-information',
                loadChildren: './packet-information/packet-information.module#CensorshipDetectorPacketInformationModule'
            },
            {
                path: 'packet-information',
                loadChildren: './packet-information/packet-information.module#CensorshipDetectorPacketInformationModule'
            },
            {
                path: 'packet-information',
                loadChildren: './packet-information/packet-information.module#CensorshipDetectorPacketInformationModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CensorshipDetectorEntityModule {}
