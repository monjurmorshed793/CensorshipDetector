/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CensorshipDetectorTestModule } from '../../../test.module';
import { PacketInformationDetailComponent } from 'app/entities/packet-information/packet-information-detail.component';
import { PacketInformation } from 'app/shared/model/packet-information.model';

describe('Component Tests', () => {
    describe('PacketInformation Management Detail Component', () => {
        let comp: PacketInformationDetailComponent;
        let fixture: ComponentFixture<PacketInformationDetailComponent>;
        const route = ({ data: of({ packetInformation: new PacketInformation(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CensorshipDetectorTestModule],
                declarations: [PacketInformationDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(PacketInformationDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(PacketInformationDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.packetInformation).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
