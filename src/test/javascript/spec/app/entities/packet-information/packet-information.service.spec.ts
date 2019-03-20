/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { PacketInformationService } from 'app/entities/packet-information/packet-information.service';
import { IPacketInformation, PacketInformation } from 'app/shared/model/packet-information.model';

describe('Service Tests', () => {
    describe('PacketInformation Service', () => {
        let injector: TestBed;
        let service: PacketInformationService;
        let httpMock: HttpTestingController;
        let elemDefault: IPacketInformation;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(PacketInformationService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new PacketInformation(
                0,
                'AAAAAAA',
                'AAAAAAA',
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                0,
                currentDate
            );
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        lastModified: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                service
                    .find(123)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: elemDefault }));

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a PacketInformation', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        lastModified: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        lastModified: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new PacketInformation(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a PacketInformation', async () => {
                const returnedFromService = Object.assign(
                    {
                        sourceAddress: 'BBBBBB',
                        destinationAddress: 'BBBBBB',
                        window: 1,
                        identificationNumber: 1,
                        sequenceNumber: 1,
                        sourcePort: 1,
                        destinationPort: 1,
                        acknowledgeNumber: 1,
                        ttl: 1,
                        syn: 'BBBBBB',
                        fin: 'BBBBBB',
                        ack: 'BBBBBB',
                        protocol: 1,
                        lastModified: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        lastModified: currentDate
                    },
                    returnedFromService
                );
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of PacketInformation', async () => {
                const returnedFromService = Object.assign(
                    {
                        sourceAddress: 'BBBBBB',
                        destinationAddress: 'BBBBBB',
                        window: 1,
                        identificationNumber: 1,
                        sequenceNumber: 1,
                        sourcePort: 1,
                        destinationPort: 1,
                        acknowledgeNumber: 1,
                        ttl: 1,
                        syn: 'BBBBBB',
                        fin: 'BBBBBB',
                        ack: 'BBBBBB',
                        protocol: 1,
                        lastModified: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        lastModified: currentDate
                    },
                    returnedFromService
                );
                service
                    .query(expected)
                    .pipe(
                        take(1),
                        map(resp => resp.body)
                    )
                    .subscribe(body => expect(body).toContainEqual(expected));
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify([returnedFromService]));
                httpMock.verify();
            });

            it('should delete a PacketInformation', async () => {
                const rxPromise = service.delete(123).subscribe(resp => expect(resp.ok));

                const req = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
