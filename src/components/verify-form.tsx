"use client"

import { useState } from "react"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Separator } from "./ui/separator"
import { Badge } from "./ui/badge"
import { AlertCircleIcon, BadgeCheckIcon, CheckIcon } from "lucide-react"

export function VerifyForm(){
  const [isSigned, setIsSigned] = useState(false)
  
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsSigned(true)
  }
  return (
    <Card>
        <CardHeader>
          <CardTitle>Signature verification</CardTitle>
          <CardDescription>
            Enter ID or text + signature
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <div className="flex flex-col gap-6">
              <div className="grid gap-3">
                <Label htmlFor="email">ID</Label>
                <Input
                  id="id"
                  type="text"
                  placeholder="ID singnature"
                />
              </div>

              <div className="flex justify-center">
                <span className="text-muted-foreground text-sm">or</span>
              </div>

              <div className="grid gap-3">
                <Label htmlFor="text">Text</Label>
                <Textarea 
                  placeholder="Paste your text here"
                />
              </div>
              <div className="grid gap-3">
                <Label htmlFor="signature">Signature</Label>
                <Textarea 
                  placeholder="Paste your signature here"
                />
              </div>
              <div className="flex flex-col gap-3">
                <Button type="submit" className="w-full cursor-pointer">
                  Check
                </Button>
              </div>
            </div>
          </form>
        </CardContent>
      {isSigned && (
        <CardFooter className="flex">
          <div className="w-full">
            <Separator className="mb-4" />
            <Badge variant="secondary" className="bg-green-500 text-white dark:bg-green-600">
              <BadgeCheckIcon />
              Check
            </Badge>
            {/*
            <Badge variant="secondary" className="bg-red-500 text-white dark:bg-red-600">
              <BadgeCheckIcon />
              No Check
            </Badge> 
            */}            
            <div className="flex flex-wrap gap-4">
              <div>
                <Label className="text-muted-foreground">Signatory</Label>
                <div>John Cena</div>
              </div>
              <div>
                <Label className="text-muted-foreground">Algorithm</Label>
                <div>SHA-256</div>
              </div>
              <div>
                <Label className="text-muted-foreground">Date/Time</Label>
                <div>28/08/2025 17:51</div>
              </div>
            </div>
          </div>
        </CardFooter>
      )}
      </Card>
  )
}